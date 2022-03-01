package com.dhbw.tutorsystem.mails;

import java.util.Base64;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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

    @Autowired
    private SpringTemplateEngine thymleafTemplateEngine;


    public void sendRegistrationMail(String mailTo, String hashBase64) throws MessagingException {
        Context thymeleafContext = new Context();
        String linkUrl = frontendUrl + "/verify?h=" + hashBase64 + "&e=" + mailTo;
        thymeleafContext.setVariable("link", linkUrl);
        thymeleafContext.setVariable("dhbw-logo", "dhbw-logo.png");
        String htmlBody = thymleafTemplateEngine.process("registrationInfo.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Registrierung Tutorensystem");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }
}
