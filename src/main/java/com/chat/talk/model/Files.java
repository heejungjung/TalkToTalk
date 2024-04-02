package com.chat.talk.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@Getter
@Setter
@ToString
@Table(name = "profile")
@Component
//프로필사진 및 상태메시지
public class Files {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long fno;
	String username;
	String filename;
	String rawname;
	String fileurl;
	String message;
	String nickname;

	public Long getFno() {
		return fno;
	}

	public void setFno(Long fno) {
		this.fno = fno;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getRawname() {
		return rawname;
	}

	public void setRawname(String rawname) {
		this.rawname = rawname;
	}

	public String getFileurl() {
		return fileurl;
	}

	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "Files [fno=" + fno + ", username=" + username + ", filename=" + filename + ", rawname=" + rawname
				+ ", fileurl=" + fileurl + ", message=" + message + ", nickname=" + nickname + "]";
	}

}