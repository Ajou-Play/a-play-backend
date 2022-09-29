package com.paran.aplay.user.domain;

import static javax.persistence.GenerationType.IDENTITY;

import com.paran.aplay.channel.domain.Channel;
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
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_team",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unq_user_team_user_id_team_id",
            columnNames = {"user_id", "team_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTeam extends BaseEntity {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "user_team_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_team_user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_team_team_id")
  private Team team;
}
