package alivium.controller;

import alivium.model.dto.request.ChatMessageRequest;
import alivium.model.dto.request.DeleteMessageWsRequest;
import alivium.model.dto.request.EditMessageWsRequest;
import alivium.model.dto.request.ReadMessageWsRequest;
import alivium.model.dto.response.ChatMessageResponse;
import alivium.model.dto.response.MessageResponse;
import alivium.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWSController {
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request){
        ChatMessageResponse response=chatMessageService.sendMessage(request);

        messagingTemplate.convertAndSend("/topic/chat-room." + request.getChatRoomId(), response);
    }

    @MessageMapping("/chat.edit")
    public void edit(@Payload EditMessageWsRequest request){
        ChatMessageResponse response=chatMessageService.editMessage(
                request.getMessageId(),
                request.getNewMessage(),
                request.getUserId()
        );
        messagingTemplate.convertAndSend("/topic/chat-room." + request.getChatRoomId(), response);
    }

    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload DeleteMessageWsRequest request){
        MessageResponse response=chatMessageService.deleteMessage(
                request.getMessageId(),
                request.getUserId()
        );
        messagingTemplate.convertAndSend("/topic/chat-room." + request.getChatRoomId(), response);
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ReadMessageWsRequest request) {
        ChatMessageResponse response = chatMessageService.markAsRead(request.getMessageId(), request.getUserId());

        messagingTemplate.convertAndSendToUser(
                request.getUsername(),
                "/queue/read",
                response
        );
    }

}
