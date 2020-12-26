package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hse.web.configuration.SMTPPropsProvider;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
@Slf4j
public class SMTPService extends Authenticator {

    private final SMTPPropsProvider propsProvider;

    public void sendSecurityCode(String targetEmail, String code) {

        try {
            MimeMessage message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(propsProvider.getUsername()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(targetEmail));
            message.setSubject("Web App auth");
            message.setText(String.format(
                    "Your verification code: %s \nRegards,\nGreenApp team.", code));

            Transport.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        log.info("Code {} delivered to {}", code, targetEmail);

    }

    private Session getSession() {
        return Session.getInstance(propsProvider.getMailProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(propsProvider.getUsername(), propsProvider.getPassword());
            }
        });
    }
}