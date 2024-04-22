package com.chat.talk.controller;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class SMTPAuthenticator extends Authenticator {
	
    // @Value("${GOOGLE_PASSWORD}")
    private String googlepw;
	
	//메일 전송에 필요한 인증
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("ahahfnr@gmail.com", googlepw);
	}
	
}