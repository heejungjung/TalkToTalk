package com.chat.talk.controller;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.springframework.stereotype.Service;

@Service
public class SMTPAuthenticator extends Authenticator {
	
	//메일 전송에 필요한 인증
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("ahahfnr@gmail.com", "gbrcftpmugbbymfb");
	}
	
}