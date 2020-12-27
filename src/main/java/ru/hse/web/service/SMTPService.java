package ru.hse.web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.hse.web.configuration.SMTPPropsProvider;
import ru.hse.web.domain.PrivilegeEntity;
import ru.hse.web.model.AssignmentStatus;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class SMTPService extends Authenticator {

    private final SMTPPropsProvider propsProvider;
    private static final String SIGNATURE = "\n\nRegards, WebApp team. \nPlease contact us with any questions by state-privilege.helpdesk@internet.ru";
    private static final String PREFIX = "Dear, %s \n\n";

    @Async
    public void sendSecurityCodeNotification(String targetEmail, String code, String name) {
        String text = format(PREFIX, name) + format("Your verification code: %s" + SIGNATURE, code);
        send(text, targetEmail);
    }

    @Async
    public void sendAccountExpiredNotification(String targetEmail, String name) {
        String text = format(PREFIX, name) + "Your account hasn't been activated in 5 minutes. All details removed.\n" +
                "For access to the portal, please proceed account creation procedure again." +
                SIGNATURE;
        send(text, targetEmail);
    }

    @Async
    public void sendAccountActivatedNotification(String targetEmail, String name) {
        String text = format(PREFIX, name) + "Your account has been successfully activated!" + SIGNATURE;
        send(text, targetEmail);

    }

    @Async
    public void sendLoginActionNotification(String targetEmail, String name) {
        LocalDateTime now = LocalDateTime.now();
        String text = format(PREFIX, name) + format("Signed in to your account. Time: %s. If it wasn't you, please contact us immediately!", now) + SIGNATURE;
        send(text, targetEmail);
    }

    @Async
    public void sendPrivilegeUpdatedNotification(String targetEmail, String name, String privHeader) {
        String text = format(PREFIX, name) +
                format("The terms of the benefit %s have changed. Please check them on our web site.", privHeader) +
                SIGNATURE;
        send(text, targetEmail);
    }

    @Async
    public void sendAssignmentStatusUpdated(String targetEmail, String name, PrivilegeEntity privilegeEntity, AssignmentStatus status) {
        String text = format(PREFIX, name)
                + format("Status of your assignment for privilege '%s' has been changed. Current status: '%s'", privilegeEntity.getName(), status)
                + SIGNATURE;
        send(text, targetEmail);
    }

    @Async
    public void sendPrivilegeAssignedNotification(String targetEmail, PrivilegeEntity privilegeEntity, String name) {
        String text = format(PREFIX, name) + format("Congratulations!" +
                        " You have been assigned benefit %s, signed by ministry '%s'. For any additional information please visit our web-site!" + SIGNATURE,
                privilegeEntity.getName(), privilegeEntity.getLegalMinistry());
        send(text, targetEmail);
    }

    @Async
    public void sendAvailablePrivilegesNotification(String targetEmail, String name, List<PrivilegeEntity> privileges) {
        var assignment = "Name: %s \n Description: %s \n Authorized ministry: %s \n Date added to registry: %s \n\n";
        var text = format(PREFIX, name) + "We have detected that this list of privileges is available for assignment according to your info: \n %s" + SIGNATURE;
        StringBuilder sb = new StringBuilder();
        privileges.forEach(pr -> sb.append(format(assignment, pr.getName(), pr.getDescription(), pr.getLegalMinistry(), pr.getTimeCreated())).append('\n'));
        var res = format(text, sb.toString());
        send(res, targetEmail);
    }

    private void send(String text, String email) {
        try {
            MimeMessage message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(propsProvider.getUsername()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("state-privilege.gov");
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