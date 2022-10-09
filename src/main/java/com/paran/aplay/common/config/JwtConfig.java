package com.paran.aplay.common.config;

import com.paran.aplay.jwt.Jwt;
import com.paran.aplay.jwt.JwtAuthenticationFilter;
import com.paran.aplay.jwt.JwtAuthenticationProvider;
import com.paran.aplay.jwt.JwtService;
import com.paran.aplay.user.service.UserService;
import com.paran.aplay.user.service.UserUtilService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtConfig {
  private String issuer;
  private String clientSecret;
  private Token accessToken;
  private Token refreshToken;
  private String blackListPrefix;
  @Getter
  @Setter
  public static class Token {
    private String header;
    private int expirySeconds;

    @Override
    public String toString() {
      return "header: "+header+" expirySeconds: "+expirySeconds;
    }
  }

  @Bean
  @Qualifier("accessJwt")
  public Jwt accessJwt() {
    return new Jwt(
        this.issuer,
        this.clientSecret,
        this.accessToken.expirySeconds);
  }

  @Bean
  @Qualifier("refreshJwt")
  public Jwt refreshJwt() {
    return new Jwt(
        this.issuer,
        this.clientSecret,
        this.refreshToken.expirySeconds);
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService,
      UserService userService) {
    return new JwtAuthenticationProvider(jwtService, userService);
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
      UserUtilService userUtilService) {
    return new JwtAuthenticationFilter(this.accessToken.header, jwtService,
        userUtilService);
  }
}
