package com.paran.aplay.user.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Authority {
  ADMIN,
  EDITOR;

  public SimpleGrantedAuthority toGrantedAuthority() {
    return new SimpleGrantedAuthority(this.toString());
  }
}
