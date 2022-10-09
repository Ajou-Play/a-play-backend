package com.paran.aplay.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.paran.aplay.jwt.claims.AccessClaim;
import com.paran.aplay.jwt.claims.Claims;
import com.paran.aplay.jwt.claims.RefreshClaim;
import java.util.Date;
import lombok.Getter;

@Getter
public class Jwt {
  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;

  private final Algorithm algorithm;

  private final JWTVerifier jwtVerifier;

  public Jwt(String issuer, String clientSecret, int expirySeconds) {
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySeconds = expirySeconds;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
        .withIssuer(issuer)
        .build();
  }

  public String sign(Claims claims){
    Date now = new Date();
    JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
    builder.withIssuer(issuer);
    builder.withIssuedAt(now);
    if(expirySeconds > 0) {
      builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1_000L));
    }
    claims.applyToBuilder(builder);
    return  builder.sign(algorithm);
  }

  public AccessClaim verifyAccessToken(String token) throws JWTVerificationException {
    return new AccessClaim(jwtVerifier.verify(token));
  }

  public RefreshClaim verifyRefreshToken(String token) throws JWTVerificationException {
    return new RefreshClaim(jwtVerifier.verify(token));
  }
}
