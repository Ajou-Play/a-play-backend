package com.paran.aplay.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.jwt.JwtAuthenticationToken;
import com.paran.aplay.jwt.JwtPrincipal;
import com.paran.aplay.jwt.JwtService;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.request.UserSignInRequest;
import com.paran.aplay.user.dto.request.UserSignUpRequest;
import com.paran.aplay.user.dto.response.SignInResponse;
import com.paran.aplay.user.dto.response.SignUpResponse;
import com.paran.aplay.user.service.UserService;
import com.paran.aplay.user.service.UserUtilService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserController {
  private final UserService userService;

  private final UserUtilService userUtilService;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;
  @PostMapping("/local/signin")
  public ResponseEntity<ApiResponse<SignInResponse>> signIn(
      @RequestBody @Valid UserSignInRequest request) {
    JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
        request.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);
    String refreshToken = (String) authentication.getDetails();
    JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
    ApiResponse response = ApiResponse.builder()
        .message("로그인 성공하였습니다.")
        .status(OK.value())
        .data(SignInResponse.builder()
            .userId(principal.getUser().getId())
            .accessToken(principal.getAccessToken())
            .refreshToken(refreshToken)
            .build())
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.add("accessToken", principal.getAccessToken());
    headers.add("refreshToken", refreshToken);
    return ResponseEntity.ok()
            .headers(headers)
            .body(response);
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid
  UserSignUpRequest request) {
    User newUser = userService.signUp(request);
    ApiResponse response = ApiResponse.builder()
        .message("회원가입 성공하였습니다.")
        .status(CREATED.value())
        .data(SignUpResponse.from(newUser))
        .build();
    return ResponseEntity.created(URI.create("/signup")).body(response);
  }
}
