package com.paran.aplay.chat.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static javax.persistence.GenerationType.IDENTITY;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chat {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User writer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  @Lob
  private String content;

  @Transient
  private MessageType type;

  @Builder
  public Chat(User writer, Channel channel, String content) {
    if (!hasText(content)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    this.writer = writer;
    this.channel = channel;
    this.content = content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public enum MessageType {
    ENTER, TALK
  }
}
