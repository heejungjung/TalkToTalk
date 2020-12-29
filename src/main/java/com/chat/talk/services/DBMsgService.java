package com.chat.talk.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.talk.model.DBMsg;
import com.chat.talk.model.Message;
import com.chat.talk.repository.DBMsgRepository;
import com.chat.talk.repository.UserRepository;

@Service
public class DBMsgService {
	
	@Autowired
	DBMsgRepository dbMsgRepository;
	
	@Autowired
	UserRepository userRepository;

    public DBMsg dbmsg(Message message) {
    	DBMsg dbmsg = new DBMsg();
    	dbmsg.setMsgtype("chat");
    	dbmsg.setContent(message.getContent());
    	dbmsg.setSex(message.getSex());
    	dbmsg.setTime(now());
    	dbmsg.setSenderid(userRepository.findByNickname(message.getSender()).getUsername());
    	
        return dbMsgRepository.save(dbmsg);
    }

    public DBMsg dbmsgleave(Message message) {
    	DBMsg dbmsg = new DBMsg();
    	dbmsg.setMsgtype("leave");
    	dbmsg.setContent(message.getContent());
    	dbmsg.setSex(message.getSex());
    	dbmsg.setTime(now());
    	dbmsg.setSenderid(userRepository.findByNickname(message.getSender()).getUsername());
    	
        return dbMsgRepository.save(dbmsg);
    }

    public DBMsg dbmsgenter(Message message) {
    	DBMsg dbmsg = new DBMsg();
    	dbmsg.setMsgtype("join");
    	dbmsg.setContent(message.getContent());
    	dbmsg.setSex(message.getSex());
    	dbmsg.setTime(now());
    	dbmsg.setSenderid(userRepository.findByNickname(message.getSender()).getUsername());
    	
        return dbMsgRepository.save(dbmsg);
    }

    public DBMsg dbmsgnotice(Message message) {
    	DBMsg dbmsg = new DBMsg();
    	dbmsg.setMsgtype("notice");
    	dbmsg.setContent(message.getContent());
    	dbmsg.setSex(message.getSex());
    	dbmsg.setTime(now());
    	dbmsg.setSenderid(userRepository.findByNickname(message.getSender()).getUsername());
    	
        return dbMsgRepository.save(dbmsg);
    }
    
    public String now() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = new Date();
    	String time = format.format(date);
    	
    	return time;
    }
}
