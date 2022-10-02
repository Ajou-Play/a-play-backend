package com.paran.aplay.team.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "team_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  public Team(String name) {
    if(!hasText(name)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
  }
}
