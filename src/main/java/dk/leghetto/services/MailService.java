package dk.leghetto.services;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MailService {
    @Inject
    Mailer mailer;

    public void sendMail(String to, String subject, String body) {
        mailer.send(Mail.withText(to, subject, body));
    }
}
