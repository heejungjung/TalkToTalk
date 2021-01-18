package com.chat.talk.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.talk.model.ChatRoom;
import com.chat.talk.model.DBRoom;
import com.chat.talk.repository.RoomListRepository;
import com.chat.talk.repository.SearchListRepository;

@Service
public class RoomListService {
	
	@Autowired
	RoomListRepository roomListRepository;
	
	@Autowired
	SearchListRepository searchListRepository; 
	   
	//채팅방 이름 검색
	public List<ChatRoom> searchService(String search) {
       List<DBRoom> rooms = searchListRepository.findByTitleContains(search);
       List<ChatRoom> result = new ArrayList<ChatRoom>();
       if(rooms.isEmpty()) return result;
       
       for(DBRoom r : rooms)
       {   
           ChatRoom chatRoom = new ChatRoom();
           chatRoom.setRoomid(r.getTitle());
           chatRoom.setNowpp(r.getPeople());
           chatRoom.setMaxpp(r.getPeoplemax());
           chatRoom.setRoomtype(r.getType());
           result.add(chatRoom);
       }
        return result;
    }

	//채팅방 만들기
    public DBRoom addroom(ChatRoom chatRoom) {
    	DBRoom room = new DBRoom();
    	room.setTitle(chatRoom.getRoomid());
    	room.setPeople(0);
    	room.setPeoplemax(chatRoom.getMaxpp());
    	room.setType(chatRoom.getRoomtype());
        return roomListRepository.save(room);
    }

	//채팅방 리스트
    public List<ChatRoom> List_All() {
    	List<DBRoom> rooms = roomListRepository.findAll();
        List<ChatRoom> result = new ArrayList<ChatRoom>();
    	
    	for(DBRoom r : rooms)
    	{
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setRoomid(r.getTitle());
            chatRoom.setNowpp(r.getPeople());
            chatRoom.setMaxpp(r.getPeoplemax());
            chatRoom.getRoomtype();
    		result.add(chatRoom);
    	}
        return result;
    }

	//채팅방 입장
    public void Join(String roomid) {
    	DBRoom room = roomListRepository.findByTitle(roomid);
    	increment(room);
    }

	//채팅방 퇴장
    public void Leave(String curroomid) {
    	DBRoom room = roomListRepository.findByTitle(curroomid);
    	decrement(room);
    }

	//채팅방 인원 증가
    public void increment(DBRoom room) {
		room.setPeople(room.getPeople() + 1); 
		roomListRepository.save(room);
    }

	//채팅방 인원 감소
    public void decrement(DBRoom room) {
		room.setPeople(room.getPeople() - 1);
    	int people = room.getPeople();
    	if(people==0) {
    		roomListRepository.delete(room);
    	}
    	else {
    		roomListRepository.save(room);
    	}
    }

	//채팅방 공지 등록
    public void Notice(String roomid,String notice) {
    	DBRoom room = roomListRepository.findByTitle(roomid);
    	room.setNotice(notice);
		roomListRepository.save(room);
    }

	//채팅방 공지 가져오기
    public String getNotice(String roomid) {
    	String result = roomListRepository.findByTitle(roomid).getNotice();
		return result;
    }

	//채팅방 현재 인원
    public int nowpeople(String roomid) {
    	return roomListRepository.findByTitle(roomid).getPeople();
    }

	//채팅방 최대 인원
    public int maxpeople(String roomid) {
    	return roomListRepository.findByTitle(roomid).getPeoplemax();
    }
}