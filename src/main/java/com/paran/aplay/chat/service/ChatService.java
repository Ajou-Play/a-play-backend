package com.paran.aplay.chat.service;

import static com.paran.aplay.common.ErrorCode.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.channel.service.ChannelService;
import com.paran.aplay.channel.service.ChannelUtilService;
import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.dto.ChatRequest;
import com.paran.aplay.chat.dto.ChatResponse;
import com.paran.aplay.chat.repository.ChatRepository;
import com.paran.aplay.common.error.exception.NotFoundException;
import com.paran.aplay.common.util.RedisService;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.service.UserUtilService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ObjectMapper objectMapper;

  private final UserUtilService userUtilService;

  private final ChannelUtilService channelUtilService;

  private final ChatRepository chatRepository;

  private final ChannelTopic channelTopic;

  private final RedisService redisService;
  @Transactional
  public void sendMessage(ChatRequest request) {
    ChatMessage message = createChatMessage(request);
    ChatResponse response = ChatResponse.from(message);
    redisService.publishChatMessage(channelTopic, response);
  }

  @Transactional
  public ChatMessage createChatMessage (ChatRequest chatRequest) {
    User sender = userUtilService.getUserById(chatRequest.getSenderId());
    Channel channel = channelUtilService.getChannelById(chatRequest.getChannelId());
    ChatMessage chatMessage = ChatMessage.builder()
        .sender(sender)
        .channel(channel)
        .content(chatRequest.getContent())
        .build();
    chatMessage.setMessageType(chatRequest.getType());
    return chatRepository.save(chatMessage);
  }

  @Transactional(readOnly = true)
  public Page<ChatResponse> getChatMessages(Long channelId, Pageable pageable) {
    channelUtilService.validateChannelId(channelId);
    Page<ChatMessage> chatMessages = chatRepository.findChatMessageByChannelId(channelId, pageable);
    return chatMessages.map(ChatResponse::from);
  }

  @Transactional(readOnly = true)
  public ChatMessage getChatMessageById(Long messageId) {
    return chatRepository.findById(messageId).orElseThrow(() -> new NotFoundException(CHAT_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<ChatMessage> getAllChatMessages() {
    return chatRepository.findAll();
  }
}
