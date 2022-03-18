package com.dhbw.tutorsystem.Mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.rules.ExternalResource;

import javax.mail.internet.MimeMessage;

public class SMTPServerRule extends ExternalResource {

    private GreenMail smtpServer;
    private int port;

    public SMTPServerRule(int port){
        this.port = port;
    }

    @Override
    public void before() throws Throwable{
        super.before();
        smtpServer = new GreenMail(new ServerSetup(port,null,"smtp"));
        smtpServer.start();
    }

    public MimeMessage[] getMessages() {
        return smtpServer.getReceivedMessages();
    }

    @Override
    public void after() {
        super.after();
        smtpServer.stop();
    }
}
