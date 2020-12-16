package com.chat.talk.model;

public class Hashtag
{
    String hid;
    String rname;
    String hashtag;
    
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getRname() {
		return rname;
	}
	public void setRname(String rname) {
		this.rname = rname;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
	
	@Override
	public String toString() {
		return "Hashtag [hid=" + hid + ", rname=" + rname + ", hashtag=" + hashtag + "]";
	}
    
}
