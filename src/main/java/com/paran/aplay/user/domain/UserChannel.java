package com.paran.aplay.user.domain;

import com.paran.aplay.channel.domain.Channel;
import com.paran.aplay.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_channel",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unq_user_channel_user_id_channel_id",
            columnNames = {"user_id", "channel_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChannel extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_channel_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id")
  private Channel channel;

  public UserChannel(User user, Channel channel) {
    this.user = user;
    this.channel = channel;
  }
}
