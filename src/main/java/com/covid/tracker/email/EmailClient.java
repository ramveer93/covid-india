package com.covid.tracker.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailClient {
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	public void sendEmail() throws Exception{
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo("rsverma1193@hotmail.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");
        msg.setFrom("covidapp@test.com");
        javaMailSender.send(msg);
	}

}
