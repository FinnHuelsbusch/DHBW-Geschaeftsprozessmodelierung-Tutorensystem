package com.dhbw.tutorsystem.mails;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private void sendMimeMessage(MimeMessage email) {
        javaMailSender.send(email);
    }

    private MimeMessage getMimeMessage() {
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
        } else if (mailType == MailType.TUTORIAL_PARTICIPATION) {
            sendTutorialParticipationMail(mailTo, arguments);
        } else if (mailType == MailType.UNREGISTERD_USER_ADDED_TO_TUTORIAL) {
            sendUnregisterdUserAddedToTutorialAsTutorMail(mailTo, arguments);
        } else if(mailType == MailType.USER_ADDED_TO_TUTORIAL){
            sendUserAddedToTutorialAsTutorMail(mailTo, arguments); 
        } else {
            throw new IllegalArgumentException("MailType is not known.");
        }
    }

    private void sendRegistrationMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        String hashBase64 = (String) arguments.get("hashBase64");
        boolean isFirstRegisterMail = (boolean) arguments.get("isFirstRegisterMail");

        Context thymeleafContext = new Context();
        String linkUrl = frontendUrl + "/verifyRegistration?h=" + hashBase64 + "&e=" + mailTo;
        thymeleafContext.setVariable("link", linkUrl);
        thymeleafContext.setVariable("isFirstRegisterMail", isFirstRegisterMail);
        String htmlBody = thymeTemplateEngine.process("./Email-Template/E-Mail-Registrierung/Email-Template_Registrierung.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Registrierung");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendResetPasswordMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        String hashBase64 = (String) arguments.get("hashBase64");

        Context thymeleafContext = new Context();
        String linkUrl = frontendUrl + "/verifyResetPassword?h=" + hashBase64 + "&e=" + mailTo;
        thymeleafContext.setVariable("link", linkUrl);
        String htmlBody = thymeTemplateEngine.process("resetPasswordMail.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Passwort vergessen");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

<<<<<<< HEAD
=======
    private void sendTutorialParticipationMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        Integer tutorialId = (Integer) arguments.get("tutorialId");

        Context thymeleafContext = new Context();
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("tutorialLinkUrl", tutorialLinkUrl);
        String htmlBody = thymeTemplateEngine.process("tutorialParticipationMail.html", thymeleafContext);

        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Teilnahme am Tutorium");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }
    
>>>>>>> origin/development
    private void sendUnregisterdUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        
    }

    private void sendUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        
    }
}
