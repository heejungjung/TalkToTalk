package com.chat.talk.model;

import java.util.List;

public class ChatRoom
{
    String roomid;
    int nowpp;
    int maxpp;
    List<Message> messages;
    
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public int getNowpp() {
		return nowpp;
	}
	public void setNowpp(int nowpp) {
		this.nowpp = nowpp;
	}
	public int getMaxpp() {
		return maxpp;
	}
	public void setMaxpp(int maxpp) {
		this.maxpp = maxpp;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	@Override
	public String toString() {
		return "ChatRoom [roomid=" + roomid + ", nowpp=" + nowpp + ", maxpp=" + maxpp + ", messages=" + messages + "]";
	}
	
}
