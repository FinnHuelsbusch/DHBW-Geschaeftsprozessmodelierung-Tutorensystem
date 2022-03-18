package com.dhbw.tutorsystem.Mail;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class POCMail {

	@Autowired
	private JavaMailSender javaMailSender;

	public SMTPServerRule smtpServerRule = new SMTPServerRule(2525);

	@BeforeEach
	void startSMTPServer() throws Throwable {
		smtpServerRule.before();
	}

	@AfterEach
	void tearDown() {
		smtpServerRule.after();
	}


	@Test
	void sendMail() throws Exception {
		MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true, "utf-8");
		helper.setTo("example@dhbw.com");
		helper.setSubject("TestMail Registrierung");
		helper.setText("kjahsdkjhasdjka", false);

		javaMailSender.send(helper.getMimeMessage());

		MimeMessage[] receivedMessages = smtpServerRule.getMessages();
		assertEquals(1, receivedMessages.length);

		MimeMessage current = receivedMessages[0];
		MimeMessageParser parser = new MimeMessageParser(current);
		parser.parse();

		assertEquals("TestMail Registrierung", current.getSubject());
		assertEquals("example@dhbw.com", current.getAllRecipients()[0].toString());
		assertEquals("kjahsdkjhasdjka", parser.getPlainContent());
	}

}
