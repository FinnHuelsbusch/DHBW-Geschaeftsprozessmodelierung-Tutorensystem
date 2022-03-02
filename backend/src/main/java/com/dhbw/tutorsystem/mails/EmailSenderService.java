package com.dhbw.tutorsystem.mails;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final SpringTemplateEngine thymeTemplateEngine;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender, SpringTemplateEngine thymeTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.thymeTemplateEngine = thymeTemplateEngine;
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

    public void sendMail(String mailTo, MailType mailType) throws MessagingException {
        sendMail(mailTo, mailType, null);
    }

    public void sendMail(String mailTo, MailType mailType, Map<String, Object> arguments) throws MessagingException {
        if (mailType == MailType.REGISTRATION) {
            sendRegistrationMail(mailTo, arguments);
        } else if (mailType == MailType.RESET_PASSWORD) {
            sendResetPasswordMail(mailTo, arguments);
        } else {
            throw new IllegalArgumentException("MailType is not known.");
        }
    }

    public void sendRegistrationMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        String hashBase64 = (String) arguments.get("hashBase64");
        Context thymeleafContext = new Context();
        String linkUrl = frontendUrl + "/verify?h=" + hashBase64 + "&e=" + mailTo;
        thymeleafContext.setVariable("link", linkUrl);
        String htmlBody = thymeTemplateEngine.process("registrationActivationMail.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Registrierung Tutorensystem");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    public void sendResetPasswordMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        String hashBase64 = (String) arguments.get("hashBase64");
        Context thymeleafContext = new Context();
        String linkUrl = frontendUrl + "/resetPassword?h=" + hashBase64 + "&e=" + mailTo;
        thymeleafContext.setVariable("link", linkUrl);
        String htmlBody = thymeTemplateEngine.process("resetPasswordMail.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Registrierung Tutorensystem");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }
}
