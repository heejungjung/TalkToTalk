package com.chat.talk.model;

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
@Table(name="dbroom")
//DB채팅방
public class DBRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long lno;
	
	String title;
	int people;
	int peoplemax;
	String notice;
    char type;
	public Long getLno() {
		return lno;
	}
	public void setLno(Long lno) {
		this.lno = lno;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	public int getPeoplemax() {
		return peoplemax;
	}
	public void setPeoplemax(int peoplemax) {
		this.peoplemax = peoplemax;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "DBRoom [lno=" + lno + ", title=" + title + ", people=" + people + ", peoplemax=" + peoplemax
				+ ", notice=" + notice + ", type=" + type + "]";
	}
}
