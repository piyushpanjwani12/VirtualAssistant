/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author DELL
 */
public class CheckMail {
    Properties emailProperties;
	Session mailSession;
	MimeMessage emailMessage;

	public static void send(String from,String password,String to,String sub,String msg) throws AddressException,
			MessagingException {

		CheckMail javaEmail = new CheckMail();

		javaEmail.setMailServerProperties();
		javaEmail.createEmailMessage(to,sub,msg);
		javaEmail.sendEmail();
	}

	public void setMailServerProperties() {

		String emailPort = "587";//gmail's smtp port

		emailProperties = System.getProperties();
		emailProperties.put("mail.smtp.port", emailPort);
		emailProperties.put("mail.smtp.auth", "true");
		emailProperties.put("mail.smtp.starttls.enable", "true");
                emailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

	}

	public void createEmailMessage(String to, String sub, String msg) throws AddressException,
			MessagingException {
		String toEmails =  to ;
		String emailSubject = sub;
		String emailBody = msg;

		mailSession = Session.getDefaultInstance(emailProperties, null);
		emailMessage = new MimeMessage(mailSession);

		
			emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails));
		

		emailMessage.setSubject(emailSubject);
		emailMessage.setContent(emailBody, "text/html");//for a html email
		//emailMessage.setText(emailBody);// for a text email

	}

	public void sendEmail() throws AddressException, MessagingException {

		String emailHost = "smtp.gmail.com";
		String fromUser = "piyushpanjwani12";//just the id alone without @gmail.com
		String fromUserEmailPassword = "piyush1112";

		Transport transport = mailSession.getTransport("smtp");

		transport.connect(emailHost, fromUser, fromUserEmailPassword);
		transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
		transport.close();
		System.out.println("Email sent successfully.");
	}
}
