package com.paran.aplay.chat.domain;

import static javax.persistence.GenerationType.IDENTITY;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.user.domain.User;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

public class Chat {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_user_id")
  private User writer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_channel_id")
  private Channel channel;

  @Lob
  private String content;
}
