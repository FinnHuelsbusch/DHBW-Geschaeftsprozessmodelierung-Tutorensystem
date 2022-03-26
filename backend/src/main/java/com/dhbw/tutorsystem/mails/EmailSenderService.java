package com.dhbw.tutorsystem.mails;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.util.MapUtils;
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

    // decide which type of mail needs to be sent
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
            case UNREGISTERED_TUTORIAL_TUTOR_ADDED:
                sendUnregisterdUserAddedToTutorialAsTutorMail(mailTo, arguments);
                break;
            case TUTORIAL_DELETION:
                sendTutorialDelete(mailTo, arguments);
                break;
            case TUTORIAL_TUTOR_ADDED:
                sendUserAddedToTutorialAsTutorMail(mailTo, arguments);
                break;
            case TUTORIAL_TUTOR_REMOVED:
                sendUserRemovedAsTutor(mailTo, arguments);
                break;
            case TUTORIAL_CHANGED:
                sendTutorialChanged(mailTo, arguments);
                break;
            default:
                // throw exception when an illegal argument is given
                throw new IllegalArgumentException("MailType is not known.");
        }
    }

    // send mail for all mailadresses in the set
    public void sendMails(Set<String> mailsTo, MailType mailType, Map<String, Object> arguments)
            throws MessagingException {
        for (String mailTo : mailsTo) {
            sendMail(mailTo, mailType, arguments);
        }
    }

    // send mail for all mailadresses in the set
    public void sendPersonalMails(MailType mailType, Map<String, Object> sharedArguments,
            Set<Map<String, Object>> personalArguments)
            throws MessagingException {

        for (Map<String, Object> userArgs : personalArguments) {
            // combine shared arguments with one map of personal arguments
            Map<String, Object> arguments = new HashMap<>(userArgs);
            arguments.putAll(sharedArguments);

            sendMail(
                    (String) arguments.get("email"),
                    mailType,
                    arguments);
        }
    }

    private void sendRegistrationMail(String mailTo, Map<String, Object> arguments) throws MessagingException {
        // get variables from the arguments set
        String hashBase64 = (String) arguments.get("hashBase64");
        boolean isFirstRegisterMail = (boolean) arguments.get("isFirstRegisterMail");
        String linkUrl = frontendUrl + "/verifyRegistration?h=" + hashBase64 + "&e=" + mailTo;
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("link", linkUrl);
        thymeleafContext.setVariable("isFirstRegisterMail", isFirstRegisterMail);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastname", lastame);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("user/registrationActivationMail.html", thymeleafContext);

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
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("link", linkUrl);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastname", lastame);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("user/resetPasswordMail.html", thymeleafContext);

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
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("tutorialLinkUrl", tutorialLinkUrl);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastname", lastame);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialParticipationMail.html", thymeleafContext);

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
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastname", lastame);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialParticipationRemovalMail.html",
                thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Student hat sich aus Tutorium entfernt");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendTutorialDelete(String mailTo, Map<String, Object> arguments) throws MessagingException {
        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastame", lastame);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialDeletedMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem Tutorium entfernt");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    private void sendUserRemovedAsTutor(String mailTo, Map<String, Object> arguments) throws MessagingException {
        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");
        Integer tutorialId = (Integer) arguments.get("tutorialId");
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastame", lastame);
        thymeleafContext.setVariable("link", tutorialLinkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialTutorRemovedMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem du wurdest als Tutor entfernt");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    // send to participants and tutors (tutors only if they were not removed or
    // added)
    private void sendTutorialChanged(String mailTo, Map<String, Object> arguments) throws MessagingException {

        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");
        Integer tutorialId = (Integer) arguments.get("tutorialId");
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastame", lastame);
        thymeleafContext.setVariable("link", tutorialLinkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialChangedMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem ein Tutorium wurde durch den Studiengangsleiter geändert");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    // send to all Tutors that were added to a tutorial and are registerd in the
    // system
    private void sendUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {

        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        String firstname = (String) arguments.get("firstname");
        String lastame = (String) arguments.get("lastame");
        Integer tutorialId = (Integer) arguments.get("tutorialId");
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("firstname", firstname);
        thymeleafContext.setVariable("lastame", lastame);
        thymeleafContext.setVariable("link", tutorialLinkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialTutorAddedMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem du wurdest als Tutor hinzugefügt");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());
    }

    // send to all Tutors that were added to a tutorial and are NOT registerd in the
    // system
    // Because they are not registerd we do not have the first and lastname
    private void sendUnregisterdUserAddedToTutorialAsTutorMail(String mailTo, Map<String, Object> arguments)
            throws MessagingException {

        // get variables from the arguments set and create new ones
        String tutorialTitle = (String) arguments.get("tutorialTitle");
        Integer tutorialId = (Integer) arguments.get("tutorialId");
        String tutorialLinkUrl = frontendUrl + "/tutorials/" + tutorialId;
        String registerLinkUrl = frontendUrl + "/register";

        // initialize thymeleaf
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("tutorialTitle", tutorialTitle);
        thymeleafContext.setVariable("link", tutorialLinkUrl);
        thymeleafContext.setVariable("registerLink", registerLinkUrl);

        // create html body by replacing all thymeleaf vars
        String htmlBody = thymeTemplateEngine.process("tutorial/tutorialUnregisterdTutorAddedMail.html", thymeleafContext);

        // send mail by creating mime message
        MimeMessageHelper helper = new MimeMessageHelper(getMimeMessage(), true, "utf-8");
        helper.setTo(mailTo);
        helper.setSubject("Tutorensystem du wurdest als Tutor hinzugefügt");
        helper.setText(htmlBody, true);
        sendMimeMessage(helper.getMimeMessage());

    }

}
