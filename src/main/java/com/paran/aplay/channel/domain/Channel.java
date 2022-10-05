package com.paran.aplay.channel.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static javax.persistence.GenerationType.IDENTITY;
import static org.springframework.util.StringUtils.*;
import static java.util.Objects.*;

import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import com.paran.aplay.team.domain.Team;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
