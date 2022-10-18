package com.paran.aplay.socket;

import com.paran.aplay.chat.dto.ChatResponse;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class MessageFrameHandler<T> implements StompFrameHandler {

  private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

  private final Class<T> tClass;

  public MessageFrameHandler(Class<T> tClass) {
    this.tClass = tClass;
  }

  @Override
  public Type getPayloadType(StompHeaders headers) {
    return this.tClass;
  }

  @Override
  public void handleFrame(StompHeaders headers, Object payload) {
    completableFuture.complete((T)payload);
  }

  public CompletableFuture<T> getCompletableFuture() {
    return completableFuture;
  }
}
