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
@Table(name="profile")
@Component
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

}