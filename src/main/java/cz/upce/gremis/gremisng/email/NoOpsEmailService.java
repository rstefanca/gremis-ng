package cz.upce.gremis.gremisng.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Log4j2
@Component
@Profile("dev")
@RequiredArgsConstructor
class NoOpsEmailService implements EmailService {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void send(EmailMessage emailMessage) {
        log.info("Sending email: {} - {}", emailMessage.subject(), emailMessage.body());
        applicationEventPublisher.publishEvent(new EmailSentEvent(this, emailMessage.subject(), emailMessage.to()));
    }

    @Override
    public void send(Collection<EmailMessage> emailMessages) {
        log.info("Sending {} emails", emailMessages.size());
        emailMessages.forEach(this::send);
    }
}
