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
public class DBRoom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long lno;
	
	String title;
	int people;
	int peoplemax;
	String notice;
    char type;
	
}
