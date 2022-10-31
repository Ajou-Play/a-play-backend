package com.paran.aplay.common.util;

import com.paran.aplay.chat.domain.ChatMessage;
import com.paran.aplay.chat.dto.ChatResponse;
import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
  private final RedisTemplate<String, Object> redisTemplate;

  public RedisService(
      RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setValues(String key, String data) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    values.set(key, data);
  }

  public void setValues(String key, String data, Duration duration) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    values.set(key, data, duration);
  }

  public Object getValues(String key) {
    ValueOperations<String, Object> values = redisTemplate.opsForValue();
    return values.get(key);
  }

  public void deleteValues(String key) {
    redisTemplate.delete(key);
  }

  public void publishChatMessage(ChannelTopic topic, ChatResponse message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }
}
