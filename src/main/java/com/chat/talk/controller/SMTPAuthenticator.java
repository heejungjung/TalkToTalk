package com.chat.talk.services;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication("ahahfnr@gmail.com", "gbrcftpmugbbymfb");
	}
}