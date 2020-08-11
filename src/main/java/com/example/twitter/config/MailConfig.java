package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("")
    private String host;
    @Value("")
    private String username;
    @Value("")
    private String password;
    @Value("")
    private int port;
    @Value("")
    private String protocol;
    @Value("")
    private String debug;
    @Value("")
    private String auth;
    @Value("")
    private String enable;

    @Bean
    public JavaMailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPort(port);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("", protocol);
        properties.setProperty("", debug);
        properties.setProperty("", auth);
        properties.setProperty("", enable);
        return mailSender;
    }

}
