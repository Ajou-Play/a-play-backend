package com.paran.aplay.user.domain;

import static com.paran.aplay.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.paran.aplay.common.entity.BaseEntity;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.paran.aplay.common.util.OciObjectStorageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class User extends BaseEntity {

  public static final String DEFAULT_PROFILE_IMAGE_URL =
          OciObjectStorageUtil.OBJECT_STORAGE_SERVER_URL + OciObjectStorageUtil.USER_PROFILE_IMAGE_PREFIX + "default.png";
  private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

  private static final String NAME_REGEX = "[a-zA-Z가-힣]+( [a-zA-Z가-힣]+)*";
  private static final int MAX_EMAIL_LENGTH = 100;
  private static final int MAX_NAME_LENGTH = 10;
  private static final int MAX_PROFILEIMAGE_LENGTH = 300;

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = MAX_EMAIL_LENGTH)
  private String email;

  @Column(length = MAX_PROFILEIMAGE_LENGTH)
  private String profileImage = "";

  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  private Boolean isQuit = false;

  @Enumerated(EnumType.STRING)
  private Authority authority = Authority.ADMIN;

  public User(String email, String name, Authority authority) {
    if (!hasText(email)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    if (!hasText(name)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }

    validateEmail(email);
    validateName(name);

    this.authority = authority;
    this.email = email;
    this.name = name;
    this.profileImage = DEFAULT_PROFILE_IMAGE_URL;
  }

  public void updateAuthority(Authority authority) {
    this.authority = authority;
  }
  public void updateProfileImage(String profileImage) {
    this.profileImage = profileImage;
  }
  private static void validateName(String name) {
    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if (!Pattern.matches(NAME_REGEX, name)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateEmail(String email) {
    if (email.length() > MAX_EMAIL_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if (!Pattern.matches(EMAIL_REGEX, email)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }
}
