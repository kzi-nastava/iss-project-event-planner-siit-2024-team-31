package com.example.eventplanner.service;

import com.example.eventplanner.utils.types.SMTPEmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}") private String sender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public String sendTestEmail(SMTPEmailDetails details) {
        try {

//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(sender);
//            message.setTo(details.getTo());
//            message.setSubject(details.getSubject());
//            message.setText(details.getBody());
//


            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        //    String htmlMsg = "<h3>Hello World!</h3>";
//mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
            helper.setText(details.getBody(), true); // Use this or above line.
            helper.setTo(details.getTo());
            helper.setSubject(details.getSubject());
            helper.setFrom(sender);
            emailSender.send(message);


      //      emailSender.send(message);
            return "Mail sent successfully";

        } catch (Exception e) {
            return "Mail sent error: " + e.getMessage();
        }
    }

    public void sendSimpleMessage(String to, String subject, String text, String link) {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("uns.account@inbox.lv");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text + "<br><a href=\"" + link + "\">Click here</a>", true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
    }
}