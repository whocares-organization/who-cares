package applicationtest;

import application.SendRealEmail;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendRealEmailTest {

    @Test
    void sendEmail_ShouldCallTransportSend_WithCorrectMessage() throws Exception {
        String username = "test@gmail.com";
        String password = "pass1234";
        SendRealEmail service = new SendRealEmail(username, password);

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            service.sendEmail("recipient@test.com", "Hello", "Body text");
            transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void sendEmail_ShouldThrowException_WhenRecipientIsNull() {
        SendRealEmail service = new SendRealEmail("x@gmail.com", "pwd");

        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail(null, "Sub", "Body")
        );
    }

    @Test
    void sendEmail_ShouldWrapMessagingException() throws Exception {
        SendRealEmail service = new SendRealEmail("x@gmail.com", "pwd");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            transportMock.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new jakarta.mail.MessagingException("SMTP failure"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.sendEmail("test@test.com", "Hello", "Body")
            );

            assertTrue(ex.getCause() instanceof jakarta.mail.MessagingException);
        }
    }
}
