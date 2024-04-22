package com.chat.talk.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.chat.talk.model.ChatRoom;
import com.chat.talk.model.Hashtag;
import com.chat.talk.repository.HashtagRepository;

@Service
public class HashtagService {
	
	@Autowired
	HashtagRepository hashtagRepository;

	//해쉬태그 저장
    public void addhash(ChatRoom chatRoom) {
    	String hashtag = chatRoom.getHash();
    	String roomid = chatRoom.getRoomid();
    	Timestamp now = now();
    	
    	Pattern p = Pattern.compile("\\#([0-9a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]*)");
        Matcher m = p.matcher(hashtag);
        String extractHashTag = null;
        System.err.println("im here");
        while(m.find()) {
            extractHashTag = sepcialCharacter_replace(m.group());

            if((extractHashTag != null) || (extractHashTag != "#")) {
                System.out.println("최종 추출 해시태그 : "+ extractHashTag);
            	Hashtag hash = new Hashtag();
            	hash.setRoomname(roomid);
            	hash.setRoomname(roomid);
            	hash.setHdate(now);
            	hash.setHashtag(extractHashTag);
            	hashtagRepository.save(hash);
            }
        }
    }
    
    public String sepcialCharacter_replace(String str) {
    	str = StringUtils.replace(str, "-_+=!@#$%^&*()[]{}|\\;:'\"<>,.?/~`） ","");
    	 
        if(str.length() < 1) {
        return null;
        }
     
        return str;
    }
    
    public Timestamp now() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);
    }
}
