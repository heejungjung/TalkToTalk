package com.chat.talk.controller;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.springframework.stereotype.Service;

@Service
public class SMTPAuthenticator extends Authenticator {
	
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("ahahfnr@gmail.com", "gbrcftpmugbbymfb");
	}
	
}