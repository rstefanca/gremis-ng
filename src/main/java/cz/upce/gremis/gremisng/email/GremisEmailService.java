package cz.upce.gremis.gremisng.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

@Log4j2
@Service
@Profile("prod")
class GremisEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final String from;
    private final String[] defaultEmails;

    private final ApplicationEventPublisher applicationEventPublisher;

    GremisEmailService(JavaMailSender mailSender,
                       ApplicationEventPublisher applicationEventPublisher,
                       @Value("${spring.mail.username}") String from,
                       @Value("${gremis.default-emails:josef.krupicka@upce.cz}") String[] defaultEmails) {
        this.mailSender = mailSender;
        this.applicationEventPublisher = applicationEventPublisher;
        this.from = from;
        this.defaultEmails = defaultEmails;
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void send(EmailMessage emailMessage) {
        try {
            var mimeMessage = prepareMimeMessage(emailMessage.subject(), emailMessage.to(), emailMessage.body(), emailMessage.attachments());
            mailSender.send(mimeMessage);
            log.info("Email sent - subject: {}, to: {}", emailMessage.subject(), emailMessage.to());
            applicationEventPublisher.publishEvent(new EmailSentEvent(this, emailMessage.subject(), emailMessage.to()));
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void send(Collection<EmailMessage> emailMessages) {
        LinkedHashSet<EmailMessage> localMessages = new LinkedHashSet<>(emailMessages);//async processing -> defensive copy + only unique emails
        try {
            var messages = new MimeMessage[emailMessages.size()];
            int index = 0;
            for (EmailMessage current : localMessages) {
                messages[index++] = prepareMimeMessage(current.subject(), current.to(), current.body(), current.attachments());
                applicationEventPublisher.publishEvent(new EmailSentEvent(this, current.subject(), current.to()));
            }
            mailSender.send(messages);
            log.info("Emails sent");
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }

    @NotNull
    private MimeMessage prepareMimeMessage(String subject, Collection<String> emailAddresses, String body, Collection<EmailAttachment> attachments)
            throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, !attachments.isEmpty(), "utf-8");
        helper.setFrom(from);

        if (defaultEmails.length > 0) {
            for (String defaultEmail : defaultEmails) {
                helper.addTo(defaultEmail);
            }
        } else {
            for (String to : emailAddresses) {
                helper.addTo(to);
            }
        }

        for (EmailAttachment attachment : attachments) {
            helper.addAttachment(attachment.name(), attachment.content());
        }

        helper.setSubject(subject);
        helper.setText(body, true);
        return mimeMessage;
    }
}
