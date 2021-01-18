package com.chat.talk.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@SuppressWarnings("deprecation")
@Component
public class StompHandler extends ChannelInterceptorAdapter {

    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        switch (accessor.getCommand()) {
        	//연결 됐을 때
            case CONNECT:
                System.out.println("CONNECT");
                System.out.println("sessionId: {}"+sessionId);
                System.out.println("channel:{}"+channel);
                break;
            //세션 끊겼을 때 (페이지 이동 ~ 브라우저 닫기 등)
            case DISCONNECT:
                System.out.println("DISCONNECT");
                System.out.println("sessionId: {}"+sessionId);
                System.out.println("channel:{}"+channel);
                break;
            default:
                break;
        }

    }
}