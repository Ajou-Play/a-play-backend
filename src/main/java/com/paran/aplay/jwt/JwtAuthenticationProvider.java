package com.paran.aplay.jwt;

import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.service.UserService;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtService jwtService;

  private final UserService userService;

  public JwtAuthenticationProvider(
      JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
    return processUserAuthentication(String.valueOf(jwtAuthentication.getPrincipal()), jwtAuthentication.getCredentials());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    Assert.isAssignable(authentication, JwtAuthenticationToken.class);
    return true;
  }

  private Authentication processUserAuthentication(String principal, String credentials) {
    try{
      User user = userService.signIn(principal, credentials);
      List<GrantedAuthority> authorities = List.of(user.getAuthority().toGrantedAuthority());
      String accessToken = jwtService.createAccessToken(user.getId(), user.getEmail(), authorities);
      String refreshToken = jwtService.createRefreshToken(user.getEmail());
      JwtAuthenticationToken authenticated = new JwtAuthenticationToken(new JwtPrincipal(accessToken, user), null, authorities);
      authenticated.setDetails(refreshToken);
      return authenticated;
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }
}
