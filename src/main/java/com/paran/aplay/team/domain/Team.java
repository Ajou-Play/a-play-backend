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

import com.paran.aplay.common.util.OciObjectStorageUtil;
import com.paran.aplay.team.service.TeamService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "team")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Team extends BaseEntity {

  public static final String DEFAULT_PROFILE_IMAGE_URL =
          OciObjectStorageUtil.OBJECT_STORAGE_SERVER_URL + OciObjectStorageUtil.TEAM_PROFILE_IMAGE_PREFIX + "default.png";
  private static final int MAX_PROFILEIMAGE_LENGTH = 300;

  private static final int MAX_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "team_id")
  private Long id;

  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(length = MAX_PROFILEIMAGE_LENGTH)
  private String profileImage;

  public void updateName(String name) {
    this.name = name;
  }

  public void updateProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }

  public Team(String name) {
    if(!hasText(name)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    this.name = name;
    this.profileImage = DEFAULT_PROFILE_IMAGE_URL;
  }
}
