package com.paran.aplay.channel.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static javax.persistence.GenerationType.IDENTITY;
import static org.springframework.util.StringUtils.*;
import static java.util.Objects.*;

import com.paran.aplay.chat.domain.Chat;
import com.paran.aplay.chat.domain.Chat.MessageType;
import com.paran.aplay.chat.service.ChatService;
import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.team.domain.Team;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Entity
@Table(name = "channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Channel {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "channel_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  @Transient
  private Set<WebSocketSession> sessions = new HashSet<>();

  public Channel(String name, Team team) {
    if (!hasText(name)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    if (isNull(team)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    this.name = name;
    this.team = team;
  }

  public void handleActions(WebSocketSession session, Chat chat, ChatService chatService) {
    if (chat.getType().equals(MessageType.ENTER)) {
      sessions.add(session);
      chat.setContent(chat.getWriter() + "님이 입장했습니다.");
    }
    sendMessage(chat, chatService);
  }

  public <T> void sendMessage(T message, ChatService chatService) {
    sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
  }
}
