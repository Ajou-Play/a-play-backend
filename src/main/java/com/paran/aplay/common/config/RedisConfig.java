package com.paran.aplay.common.config;

import com.paran.aplay.common.util.RedisSubscriber;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisConfig {

  public static final String MEETING_TOPIC_NAME = "meeting";

  public static final String CHAT_TOPIC_NAME = "channel";

  private final Logger log = LoggerFactory.getLogger(getClass());

  private String host;

  private int port;

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory connectionFactory,
      @Qualifier("chatMessageListener") MessageListenerAdapter chatListenerAdapter,
      @Qualifier("meetingListener") MessageListenerAdapter meetingListenerAdapter
  ){
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(chatListenerAdapter, new ChannelTopic(CHAT_TOPIC_NAME));
    container.addMessageListener(meetingListenerAdapter, new ChannelTopic(MEETING_TOPIC_NAME));
    return container;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    return redisTemplate;
  }

  @Bean("chatMessageListener")
  public MessageListenerAdapter chatListenerAdapter(RedisSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "onChatMessage");
  }

  @Bean("meetingListener")
  public MessageListenerAdapter meetingListenerAdapter(RedisSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "onMeetingMessage");
  }

}
