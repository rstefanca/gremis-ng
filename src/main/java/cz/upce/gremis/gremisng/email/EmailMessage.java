package cz.upce.gremis.gremisng.email;

import lombok.With;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@With
public record EmailMessage(Collection<String> to, String subject, String body, Collection<EmailAttachment> attachments) {

    public EmailMessage(String to, String subject, String body) {
        this(List.of(to), subject, body,new ArrayList<>());
    }
}
