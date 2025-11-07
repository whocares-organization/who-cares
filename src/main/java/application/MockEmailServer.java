package application;

import java.util.ArrayList;
import java.util.List;

public class MockEmailServer implements EmailService {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendEmail(String to, String message) {
        sentMessages.add(to + ": " + message);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clear() {
        sentMessages.clear();
    }
}
