/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.controller;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailerService {
	@Autowired
	JavaMailSender mailer;
	
	public void send(String to, String subject, String body) throws Exception {
		MimeMessage mail = mailer.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true, "utf-8");
		helper.setFrom("quangnguyenduc4@gmail.com", "E-Shop Web Master");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);
		helper.setReplyTo("quangnguyenduc4@gmail.com", "E-Shop Web Master");
		
		mailer.send(mail);
	}
}
