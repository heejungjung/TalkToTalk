package com.chat.talk.controller;

import java.util.ArrayList;
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
import com.chat.talk.services.FilesService;
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
    private FilesService filesService;
    
    @Autowired
    private UserService userService;
	
    List<ChatRoom> rooms;
    List<ChatRoom> searchRooms;

    @Autowired
    public void ChatController()
    {
        rooms = roomListService.List_All();
    }
    
    @GetMapping("/chat/search")
    public String search(@RequestParam(value = "search") String search, Model model) {

       rooms = roomListService.searchService(search);
       System.out.println("Search Rooms size: "+rooms.size());
       model.addAttribute("searchRooms", searchRooms);
       
       return "redirect:";
    }
    
    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload Message chatMessage) {
    	System.out.println("111");
    	addmessage(roomId,chatMessage);
    	chatMessage.setPic(filesService.profile(chatMessage.getSenderid()));
    	chatMessage.setSex(userService.sex(chatMessage.getSenderid()));
        messagingTemplate.convertAndSend("/room/"+roomId, chatMessage);
    }
    
    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor) {
    	System.out.println("2222");

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        addmessage(roomId,chatMessage);
        messagingTemplate.convertAndSend("/room/"+roomId, chatMessage);
        roomListService.Join(chatMessage.getRoomid());
    }

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

    @MessageMapping("/chat/rooms")
    public void addRoom(@Payload ChatRoom chatRoom)
    {
    	System.out.println("4444");
    	String roomId = chatRoom.getRoomid();
        int flag=0;
        //flag : 같은 방인지 판단
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
        }
        
        messagingTemplate.convertAndSend("/room/list", rooms);
    }

    @MessageMapping("/chat/{roomId}/leaveuser")
    public void leaveRoom(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor)
    {
    	System.out.println("555:"+roomId);
        
        Message leaveMessage = new Message();
        leaveMessage.setType(Message.MessageType.LEAVE);
        leaveMessage.setSender(chatMessage.getSender());
        addmessage(roomId,chatMessage);
        roomListService.Leave(chatMessage.getRoomid());
        messagingTemplate.convertAndSend("/room/"+roomId, leaveMessage);
    }

    private void addmessage(String roomid, Message message)
    {
    	System.out.println("666");
    	for(ChatRoom room: rooms)
        {
            if(room.getRoomid().equals(roomid))
            {
                List<Message> messages = room.getMessages();
            	//System.out.println("@@@message: "+message.toString());
                break;
            }
        }
    }

    @SubscribeMapping("chat/{roomId}/getPrevious")
    public Map<String, Object> getPreviousMessages(@DestinationVariable String roomId)
    {
    	System.out.println("7777");
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

    @MessageMapping("/chat/{roomId}/notice")
    public void notice(@DestinationVariable String roomId, @Payload Message chatMessage,SimpMessageHeaderAccessor headerAccessor)
    {
    	String roomid = chatMessage.getRoomid();
    	System.out.println("8888");
        addmessage(roomid,chatMessage);
        roomListService.Notice(chatMessage.getRoomid(),chatMessage.getContent());
        messagingTemplate.convertAndSend("/room/"+roomid, chatMessage);
    }
}