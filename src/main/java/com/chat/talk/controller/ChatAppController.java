package com.chat.talk.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chat.talk.model.ChatRoom;
import com.chat.talk.model.Message;
import com.chat.talk.services.DBMsgService;
import com.chat.talk.services.ProfilesService;
import com.chat.talk.services.HashtagService;
import com.chat.talk.services.RoomListService;
import com.chat.talk.services.UserService;

@Controller
public class ChatAppController
{
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private RoomListService roomListService;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private DBMsgService dbMsgService;

    @Autowired
    private ProfilesService ProfilesService;
    
    @Autowired
    private UserService userService;
	
    List<ChatRoom> rooms;
    List<ChatRoom> searchRooms;
    
    //메시지 저장을 위한 현재 시간 함수
    public String now() {
    	Calendar calendar = Calendar.getInstance();
    	java.util.Date date = calendar.getTime();
    	String time = (new SimpleDateFormat("H:mm:ss").format(date));
    	
    	return time;
    }
    
    //DB에서 채팅방 리스트를 불러오기 위함
    @Autowired
    public void ChatController()
    {
        rooms = roomListService.List_All();
    }
    //채팅방 제목으로 search 기능
    @GetMapping("/chat/search")
    public String search(@RequestParam(value = "search") String search, Model model) {

       rooms = roomListService.searchService(search);
       System.out.println("Search Rooms size: "+rooms.size());
       model.addAttribute("searchRooms", searchRooms);
       
       return "redirect:";
    }
    
    //특정방에 메시지 보내기
    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload Message chatMessage) {
    	addmessage(roomId,chatMessage);
    	chatMessage.setRoomid(roomId);
    	chatMessage.setPic(ProfilesService.profile(chatMessage.getSenderid()));
    	chatMessage.setSex(userService.sex(chatMessage.getSenderid()));
    	chatMessage.setTime(now());
        messagingTemplate.convertAndSend("/room/"+roomId, chatMessage);
        dbMsgService.dbmsg(chatMessage);
    }

    //특정방에 유저 입장
    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor) {
    	chatMessage.setMessageType(Message.MessageType.JOIN);

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        addmessage(roomId,chatMessage);
        messagingTemplate.convertAndSend("/room/"+roomId, chatMessage);
        roomListService.Join(chatMessage.getRoomid());
        dbMsgService.dbmsgenter(chatMessage);
    }

    //채팅방 리스트
    @SubscribeMapping("/chat/rooms")
    public List<ChatRoom> listOfRoom()
    {
        List<ChatRoom> result = new ArrayList<ChatRoom>();
    	
    	for(ChatRoom room:rooms)
        {
    		result.add(room);
        }
    	
        return result;
    }

    //채팅방 만들기
    @MessageMapping("/chat/rooms")
    public void addRoom(@Payload ChatRoom chatRoom)
    {
    	String roomId = chatRoom.getRoomid();
        int flag=0;
        //flag : 같은 방인지 판단
        System.err.println("333333333");
        System.err.println(chatRoom);
        for(ChatRoom room:rooms)
        {
            if(room.getRoomid().equals(roomId))
            {
                flag = 1;
                break;
            }
        }
        if(flag==0)
        {
        	//나중에 MESSAGE DB쌓을 곳
            List<Message> messages = new ArrayList<Message>();
            chatRoom.setMessages(messages);
            chatRoom.setNowpp(0);
            rooms.add(chatRoom);
            roomListService.addroom(chatRoom);
            hashtagService.addhash(chatRoom);
        }
        
        messagingTemplate.convertAndSend("/room/list", rooms);
    }

    //특정방에 유저 퇴장
    @MessageMapping("/chat/{roomId}/leaveuser")
    public void leaveRoom(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor)
    {
        Message leaveMessage = new Message();
        leaveMessage.setMessageType(Message.MessageType.LEAVE);
        leaveMessage.setSender(chatMessage.getSender());
        addmessage(roomId,chatMessage);
        roomListService.Leave(chatMessage.getRoomid());
        messagingTemplate.convertAndSend("/room/"+roomId, leaveMessage);
        dbMsgService.dbmsgleave(chatMessage);
    }

    //메시지 저장
    private void addmessage(String roomid, Message message)
    {
    	for(ChatRoom room: rooms)
        {
            if(room.getRoomid().equals(roomid))
            {
                List<Message> messages = room.getMessages();
                break;
            }
        }
    }

    //이전 메시지 받기
    @SubscribeMapping("chat/{roomId}/getPrevious")
    public Map<String, Object> getPreviousMessages(@DestinationVariable String roomId)
    {
    	//이전 MESSAGE DB에서 받아올 부분
        List<Message> messages = new ArrayList<Message>();
        String notice = null;
        
        for(ChatRoom room: rooms)
        {
            if(room.getRoomid().equals(roomId))
            {
            	messages = room.getMessages();
            	notice = roomListService.getNotice(roomId);
            	
                break;
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("messages", messages);
		map.put("notice", notice);
        
        return map;
    }

    //특정방에 공지 남기기
    @MessageMapping("/chat/{roomId}/notice")
    public void notice(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor)
    {
    	String roomid = chatMessage.getRoomid();
        addmessage(roomid,chatMessage);
        roomListService.Notice(chatMessage.getRoomid(),chatMessage.getContent());
        messagingTemplate.convertAndSend("/room/"+roomid, chatMessage);
        dbMsgService.dbmsgnotice(chatMessage);
    }
}