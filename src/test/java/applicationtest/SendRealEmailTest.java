package applicationtest;

import application.SendRealEmail;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    // ✅ تغطية حالة to.isBlank()
    @Test
    void sendEmail_ShouldThrowException_WhenRecipientIsBlank() {
        SendRealEmail service = new SendRealEmail("x@gmail.com", "pwd");

        assertThrows(IllegalArgumentException.class,
                () -> service.sendEmail("   ", "Sub", "Body")
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

    // ✅ تغطية فرع subject == null و body == null
    @Test
    void sendEmail_WithNullSubjectAndBody_ShouldNotThrow() {
        SendRealEmail service = new SendRealEmail("x@gmail.com", "pwd");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {
            assertDoesNotThrow(() -> service.sendEmail("recipient@test.com", null, null));
            transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    // ✅ تغطية فرع constructor لما username = null
    @Test
    void constructor_ShouldThrow_WhenUsernameNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new SendRealEmail(null, "pwd"));
    }

    // ✅ تغطية فرع constructor لما password = null
    @Test
    void constructor_ShouldThrow_WhenPasswordNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new SendRealEmail("x@gmail.com", null));
    }

    // ✅ تغطية فرع run() لما EMAIL_USERNAME/EMAIL_PASSWORD مش موجودين
    @Test
    void run_WithMissingEnv_ShouldExitGracefully() {
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            Dotenv dotenv = mock(Dotenv.class);
            dotenvMock.when(Dotenv::load).thenReturn(dotenv);
            when(dotenv.get("EMAIL_USERNAME")).thenReturn(null);
            when(dotenv.get("EMAIL_PASSWORD")).thenReturn(null);

            // Use a lambda instead of a method reference to satisfy older JUnit signatures
            assertDoesNotThrow(() -> SendRealEmail.run());
        }
    }

    // ✅ تغطية فرع run() لما في credentials صحيحة + التحقق إن Transport.send اننادى
    @Test
    void run_WithValidEnv_ShouldSendEmail() {
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            Dotenv dotenv = mock(Dotenv.class);
            dotenvMock.when(Dotenv::load).thenReturn(dotenv);
            when(dotenv.get("EMAIL_USERNAME")).thenReturn("x@gmail.com");
            when(dotenv.get("EMAIL_PASSWORD")).thenReturn("pwd");

            SendRealEmail.run();

            transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    // ✅ تغطية main() (هي بس wrapper على run)
    @Test
    void main_ShouldDelegateToRun() {
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class);
             MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            Dotenv dotenv = mock(Dotenv.class);
            dotenvMock.when(Dotenv::load).thenReturn(dotenv);
            when(dotenv.get("EMAIL_USERNAME")).thenReturn("x@gmail.com");
            when(dotenv.get("EMAIL_PASSWORD")).thenReturn("pwd");

            SendRealEmail.main(new String[]{});

            transportMock.verify(() -> Transport.send(any(Message.class)), atLeastOnce());
        }
    }
}