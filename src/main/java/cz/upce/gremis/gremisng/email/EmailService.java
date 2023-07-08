package cz.upce.gremis.gremisng.email;

import java.util.Collection;

public interface EmailService {

    void send(EmailMessage emailMessage);

    void send(Collection<EmailMessage> emailMessages);

}
