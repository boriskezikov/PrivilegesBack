package ru.hse.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@PropertySource(value = "classpath:email.props.properties")
@Configuration
public class SMTPPropsProvider {

    @Value("${mail.smtp.auth}")
    private String _auth;
    @Value("${mail.debug}")
    private String _debug;
    @Value("${mail.smtp.socketFactory.fallback}")
    private String _fallback;
    @Value("${mail.smtp.host}")
    private String _host;
    @Value("${mail.smtp.port}")
    private String _port;
    @Value("${mail.smtp.socketFactory.port}")
    private String _sPort;
    @Value("${mail.smtp.socketFactory.class}")
    private String _socketFactory;
    @Value("${mail.smtp.username}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public Properties getMailProps() {
        Properties props = new Properties();
        props.put("mail.smtp.host", _host);
        props.put("mail.debug", _debug);
        props.put("mail.smtp.auth", _auth);
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sPort);
        props.put("mail.smtp.socketFactory.class", _socketFactory);
        props.put("mail.smtp.socketFactory.fallback", _fallback);
        return props;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}