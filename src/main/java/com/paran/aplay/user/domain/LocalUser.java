package com.paran.aplay.user.domain;

import static com.paran.aplay.common.ErrorCode.*;

import com.paran.aplay.common.error.exception.AuthErrorException;
import com.paran.aplay.common.error.exception.InvalidRequestException;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@DiscriminatorValue("LOCAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends User {

  private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";

  private static final int MAX_PASSWORD_LENGTH = 500;

  @Column(length = MAX_PASSWORD_LENGTH)
  private String password;

  @Builder
  public LocalUser(String email, String name, String password, Authority authority) {
    super(email, name, authority);
    this.password = password;
  }

  public void checkPassword(PasswordEncoder passwordEncoder, String credentials) {
    if (!passwordEncoder.matches(credentials, password)) {
      throw new AuthErrorException(INVALID_ACCOUNT_REQUEST);
    }
  }

  public static void validatePassword(String password) {
    if (password.length() > MAX_PASSWORD_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if (!Pattern.matches(PASSWORD_REGEX, password)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  public void changePassword(PasswordEncoder passwordEncoder, String password) {
    validatePassword(password);
    this.password = passwordEncoder.encode(password);
  }
}
