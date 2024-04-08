package org.acme.middleware;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDate;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MailShared {
    @ConfigProperty(name = "app.mail")
    private String APP_MAIL;

    private final String subject = "Chargement de données DWH App sms 833";

    @Inject
    ReactiveMailer reactiveMailer;

    public CompletionStage<String> sendMail(String to, LocalDate date, String message) {
        //String message = checkData ? " chargées." : " manquantes.";
        String message_html = "<html><head></head><body>Bonjour, </br></br>" +
                "Données du " + date.toString() + message +
                " <br/></br>  Merci.</body></html>";
        Mail mail = Mail.withHtml(to, subject, message_html).setFrom(APP_MAIL);
        return reactiveMailer.send(mail).subscribeAsCompletionStage().thenApply(x -> "mail sent reactively");
    }
}
