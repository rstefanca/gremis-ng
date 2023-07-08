package cz.upce.gremis.gremisng.email;

import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.List;


@ToString
public final class EmailSentEvent extends ApplicationEvent {
    private final String subject;
    private final Collection<String> recipients;

    EmailSentEvent(Object source, String subject, Collection<String> recipients) {
        super(source);
        this.subject = subject;
        this.recipients = List.copyOf(recipients);
    }

    public String getSubject() {
        return subject;
    }

    public Collection<String> getRecipients() {
        return recipients;
    }


}
