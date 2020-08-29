package com.example.email.notification.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.email.notification.model.Email;

@RestController
public class Emailcontroller {
	
	Map<String, String> env = System.getenv();

	@Value("${spring.mail.username}")
	private String username;

	@Value("${spring.mail.password}")
	private String password;

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendMail(@RequestBody Email email) throws AddressException, MessagingException, IOException {
		sendEmail(email);
		return "email sent successfully";
	}

	public int sendingMail(Email email) throws AddressException, MessagingException, IOException {

		if (email.getToAddress().equalsIgnoreCase(username)) {
			return 0;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(username, false));

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getToAddress()));
		msg.setSubject(email.getSubject());
		msg.setContent(email.getBody(), "text/html");
		msg.setSentDate(new Date());

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(email.getBody(), "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		MimeBodyPart attachPart = new MimeBodyPart();

		attachPart.attachFile("/home/khushii/Downloads/3.jpg");
		multipart.addBodyPart(attachPart);
		msg.setContent(multipart);
		Transport.send(msg);
		return 1;
	}

	@Autowired
	private JavaMailSender javaMailSender;

	void sendEmail(Email email) throws MessagingException {

//		SimpleMailMessage msg = new SimpleMailMessage();
//		
//		msg.setTo(email.getToAddress());
//		msg.setSubject(email.getSubject());
//		msg.setText(email.getBody());
//
//		MimeMessage msg1 = javaMailSender.createMimeMessage();
//
//		MimeBodyPart messageBodyPart = new MimeBodyPart();
//		messageBodyPart.setContent(email.getBody(), "text/html");
//
//		Multipart multipart = new MimeMultipart();
//		multipart.addBodyPart(messageBodyPart);
//		MimeBodyPart attachPart = new MimeBodyPart();
//
//		attachPart.attachFile("/home/khushii/Downloads/3.jpg");
//		multipart.addBodyPart(attachPart);
//		msg.setContent(multipart);
//
//		javaMailSender.send(msg);

		MimeMessage msg = javaMailSender.createMimeMessage();

		// true = multipart message
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		helper.setTo(email.getToAddress());

		helper.setSubject(email.getSubject());

		// default = text/plain
		// helper.setText("Check attachment for image!");

		// true = text/html
		helper.setText(email.getBody(), true);

		helper.addAttachment("src/main/resources/3.jpg", new ClassPathResource("src/main/resources/3.jpg"));

		javaMailSender.send(msg);
	}

}