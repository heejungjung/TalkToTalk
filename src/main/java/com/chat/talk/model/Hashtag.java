package com.chat.talk.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@Entity
@Table(name = "hashtag")
//채팅방 hashtag
public class Hashtag
{
	@Id
	@Column(name="hid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    String hid;
    String roomname;
    String hashtag;
    String hdate;
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
	public String getHdate() {
		return hdate;
	}
	public void setHdate(String hdate) {
		this.hdate = hdate;
	}
	@Override
	public String toString() {
		return "Hashtag [hid=" + hid + ", roomname=" + roomname + ", hashtag=" + hashtag + ", hdate=" + hdate + "]";
	}
    
}
