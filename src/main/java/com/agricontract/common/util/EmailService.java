package com.agricontract.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String fromEmail;
//
//    public void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
//
//    public void sendRegistrationEmail(String to, String name) {
//        String subject = "Welcome to Agri Contract Platform";
//        String body = "Hello " + name + ",\n\n" +
//                "Thank you for registering on the Agri Contract Platform. We are excited to have you on board!\n\n" +
//                "Best Regards,\n" +
//                "Agri Contract Team";
//        sendEmail(to, subject, body);
//    }
//
//    public void sendLoginEmail(String to, String name) {
//        String subject = "New Login Detected";
//        String body = "Hello " + name + ",\n\n" +
//                "We detected a new login to your Agri Contract Platform account.\n" +
//                "If this wasn't you, please contact support immediately.\n\n" +
//                "Best Regards,\n" +
//                "Agri Contract Team";
//        sendEmail(to, subject, body);
//    }
//}
