package com.paran.aplay.user.domain;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("OAUTH")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User{
  @Enumerated(EnumType.STRING)
  @Column(name = "provider")
  private AuthProvider provider;

  @Column(name = "provider_id")
  private String providerId;

  @Builder
  public OAuthUser(String email, String name, AuthProvider provider, String providerId) {
    super(email, name);
    this.provider = provider;
    this.providerId = providerId;
  }
}
