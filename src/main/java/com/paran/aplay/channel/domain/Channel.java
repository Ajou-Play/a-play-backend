package com.paran.aplay.channel.domain;

import static javax.persistence.GenerationType.IDENTITY;

import com.paran.aplay.common.entity.BaseEntity;
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
  @Column(name = "team_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_team_id")
  private Team team;
}
