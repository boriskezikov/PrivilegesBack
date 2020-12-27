package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hse.web.configuration.SMTPPropsProvider;
import ru.hse.web.domain.PrivilegeEntity;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class SMTPService extends Authenticator {

    private final SMTPPropsProvider propsProvider;
    private static final String sign = "\n\nRegards, WebApp team. \nPlease contact us with any questions by state-privilege.helpdesk@internet.ru";

    @Async
    public void sendSecurityCode(String targetEmail, String code) {
        String text = String.format("Your verification code: %s" + sign, code);
        send(text, targetEmail);
    }

    @Async
    public void sendAccountExpired(String targetEmail) {
        String text = "Your account hasn't been activated in 5 minutes. All details removed.\n" +
                "For access to the portal, please proceed account creation procedure again." +
                sign;
        send(text, targetEmail);
    }

    @Async
    public void sendAccountActivated(String targetEmail) {
        String text = "Your account has been successfully activated!" + sign;
        send(text, targetEmail);

    }

    @Async
    public void sendLoginAction(String targetEmail) {
        LocalDateTime now = LocalDateTime.now();
        String text = String.format("Signed in to your account. Time: %s. If it wasn't you, please contact us immediately!", now) + sign;
        send(text, targetEmail);
    }

    public void sendPrivilegeAssigned(String targetEmail, PrivilegeEntity privilegeEntity) {
        String text = String.format("Congratulations!" +
                        " You have been assigned benefit %s, signed by ministry '%s'. For any additional information please visit our web-site!" + sign,
                privilegeEntity.getText(), privilegeEntity.getLegalMinistry());
        send(text, targetEmail);
    }

    private void send(String text, String email) {
        try {
            MimeMessage message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(propsProvider.getUsername()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("State-privilege.gov");
            message.setText(text);
            Transport.send(message);
            log.info("Message: {} delivered to: {}", text, email);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
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