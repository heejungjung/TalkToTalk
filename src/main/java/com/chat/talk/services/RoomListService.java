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
	   
    public DBRoom addroom(ChatRoom chatRoom) {
    	DBRoom room = new DBRoom();
    	room.setTitle(chatRoom.getRoomid());
    	room.setPeople(0);
    	room.setPeoplemax(chatRoom.getMaxpp());
    	room.setType(chatRoom.getRoomtype());
        return roomListRepository.save(room);
    }
	
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

    public void Join(String roomid) {
    	DBRoom room = roomListRepository.findByTitle(roomid);
    	increment(room);
    }

    public void Leave(String curroomid) {
    	DBRoom room = roomListRepository.findByTitle(curroomid);
    	decrement(room);
    }
    
    public void increment(DBRoom room) {
    	System.out.println("increment:"+room.getPeople());
		room.setPeople(room.getPeople() + 1); 
		roomListRepository.save(room);
    }

    public void decrement(DBRoom room) {
    	System.out.println("decrement1:"+room.getPeople());
    	//고쳐야함
		room.setPeople(room.getPeople() - 1);
    	int people = room.getPeople();
    	System.out.println("decrement2:"+people);
    	if(people==0) {
        	System.out.println("decrement3:"+people);
    		roomListRepository.delete(room);
    	}
    	else {
        	System.out.println("decrement4:"+people);
    		roomListRepository.save(room);
    	}
    }
    
    public void Notice(String roomid,String notice) {
    	DBRoom room = roomListRepository.findByTitle(roomid);
    	room.setNotice(notice);
		roomListRepository.save(room);
    }
    
    public String getNotice(String roomid) {
    	String result = roomListRepository.findByTitle(roomid).getNotice();
		return result;
    }
    
    public int nowpeople(String roomid) {
    	return roomListRepository.findByTitle(roomid).getPeople();
    }
    
    public int maxpeople(String roomid) {
    	return roomListRepository.findByTitle(roomid).getPeoplemax();
    }
}