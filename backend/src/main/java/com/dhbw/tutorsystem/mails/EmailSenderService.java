package com.dhbw.tutorsystem.mails;

import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Value("${backend.app.frontendUrl}")
    private String frontendUrl;

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Async
    public void sendMimeMessage(MimeMessage email) {
        javaMailSender.send(email);
    }

    public MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    public void sendRegistrationMail(String mailTo, String hashBase64) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Registrierung Tutorensystem");
        String linkUrl = frontendUrl + "/verify?h=" + hashBase64 + "&e=" + mailTo;
        helper.setText(
                "<p>Hallo,</p>" +
                        "<p>Sie haben sich im Tutorensystem registriert.</p>" +
                        "<p>Um ihr Konto zu aktivieren, klicken Sie bitte auf den nachfolgenden Link: " +
                        "<a href='" +
                        linkUrl +
                        "''>" +
                        linkUrl +
                        "</a></p>" +
                        "<p>Mit freundlichen Grüßen</p>" +
                        "<p>Ihr Tutorsystem</p>",
                true);
        sendMimeMessage(helper.getMimeMessage());
    }
}
