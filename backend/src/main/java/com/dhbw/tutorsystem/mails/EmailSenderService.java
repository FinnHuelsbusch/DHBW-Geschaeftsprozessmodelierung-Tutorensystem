package com.dhbw.tutorsystem.mails;

import java.util.Map;
import java.util.Set;

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

    // Method for mail without arguments
    public void sendMail(String mailTo, MailType mailType) throws MessagingException {
        sendMail(mailTo, mailType, null);
    }

    // Method for mails without arguments
    public void sendMails(Set<String> mailsTo, MailType mailType) throws MessagingException {
        sendMails(mailsTo, mailType, null);
    }

    // decide which type of mail needs to be send 
    public void sendMail(String mailTo, MailType mailType, Map<String, Object> arguments) throws MessagingException {

        switch (mailType) {
            case REGISTRATION:
                sendRegistrationMail(mailTo, arguments);
                break;
            case RESET_PASSWORD:
                sendResetPasswordMail(mailTo, arguments);
                break;
            case TUTORIAL_PARTICIPATION_STUDENT:
                sendTutorialParticipationStudentMail(mailTo, arguments);
                break;
            case TUTORIAL_PARTICIPATION_REMOVAL_STUDENT:
                sendTutorialParticipationStudentRemovalMail(mailTo, arguments);
                break;
            case UNREGISTERD_USER_ADDED_TO_TUTORIAL:
                sendUnregisterdUserAddedToTutorialAsTutorMail(mailTo, arguments);
                break;
            case TUTORIAL_DELETION:
                sendUTutorialDelete(mailTo, arguments);
                break;
            case USER_ADDED_TO_TUTORIAL:
                sendUserAddedToTutorialAsTutorMail(mailTo, arguments);
                break;
            default:
                // throw exception when an illegal argument is given   
                throw new IllegalArgumentException("MailType is not known.");
        }
    }

    // send mail for to all mailadresses in the set 
    public void sendMails(Set<String> mailsTo, MailType mailType, Map<String, Object> arguments)
            throws MessagingException {
        for (String mailTo : mailsTo) {
            sendMail(mailTo, mailType, arguments);
        }
    }


    private void sendRegistrationMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        // get variables from the arguments set 
        String hashBase64 = (String) arguments.get("hashBase64");
        boolean isFirstRegisterMail = (boolean) arguments.get("isFirstRegisterMail");
        String linkUrl = frontendUrl + "/verifyRegistration?h=" + hashBase64 + "&e=" + mailTo;

        // fillup thymeleaf 
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("link", linkUrl);
        thymeleafContext.setVariable("isFirstRegisterMail", isFirstRegisterMail);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("registrationActivationMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Registrierung");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendResetPasswordMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        // get variables from the arguments set and create new ones
        String hashBase64 = (String) arguments.get("hashBase64");
        String linkUrl = frontendUrl + "/verifyResetPassword?h=" + hashBase64 + "&e=" + mailTo;

        // fillup thymeleaf 
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("link", linkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("resetPasswordMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Passwort vergessen");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendTutorialParticipationStudentMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {

        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        Integer tutorialId = (Integer) arguments.get("tutorialId");
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;

        // fillup thymeleaf 
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("tutorialLinkUrl", tutorialLinkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorialParticipationMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Teilnahme am Tutorium");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendTutorialParticipationStudentRemovalMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {
                
        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");

        // fillup thymeleaf 
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorialParticipationRemovalMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Teilnahme am Tutorium");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendUnregisterdUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {

    }

    private void sendUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {

    }

    private void sendUTutorialDelete(String mailTo, Map<String, Object> arguments) throws MessagingException {

    }

}
