package com.paran.aplay.user.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


import com.paran.aplay.common.ApiResponse;
import com.paran.aplay.common.entity.CurrentUser;
import com.paran.aplay.jwt.JwtAuthenticationToken;
import com.paran.aplay.jwt.JwtPrincipal;
import com.paran.aplay.jwt.JwtService;
import com.paran.aplay.team.domain.Team;
import com.paran.aplay.team.dto.request.TeamUpdateRequest;
import com.paran.aplay.team.dto.response.TeamDetailResponse;
import com.paran.aplay.user.domain.User;
import com.paran.aplay.user.dto.request.*;
import com.paran.aplay.user.dto.response.SignInResponse;
import com.paran.aplay.user.dto.response.SignUpResponse;
import com.paran.aplay.user.dto.response.TokenResponse;
import com.paran.aplay.user.dto.response.UserDetailResponse;
import com.paran.aplay.user.service.UserService;
import com.paran.aplay.user.service.UserUtilService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

  @GetMapping("/{userId}/info")
  public ResponseEntity<ApiResponse<UserDetailResponse>> getUserInfo(@CurrentUser User user, @PathVariable("userId") Long userId) {
    User tUser = userUtilService.getUserById(userId);
    UserDetailResponse res = UserDetailResponse.from(tUser);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("유저 정보 조회 성공")
            .status(CREATED.value())
            .data(res)
            .build();
    return ResponseEntity.ok(apiResponse);
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
  @PostMapping("/token/reissue")
  public ResponseEntity<ApiResponse<SignInResponse>> reissueAccessToken(@RequestBody @Valid
                                                                        TokenReissueRequest request) {
    User user = userUtilService.getUserById(request.getUserId());
    String newAccessToken = jwtService.reissueAccessToken(user, request.getAccessToken(),
            request.getRefreshToken());
    ApiResponse apiResponse = ApiResponse.builder()
            .message("토큰이 재발급되었습니다.")
            .status(OK.value())
            .data(new TokenResponse(newAccessToken))
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PatchMapping("/me/password")
  public ResponseEntity<ApiResponse> updateUserPassword(@CurrentUser User user, @RequestBody @Valid UserUpdatePasswordRequest request) {

    userService.changeUserPassword(user, request);

    ApiResponse apiResponse = ApiResponse.builder()
            .message("비밀번호 변경 성공")
            .status(OK.value())
            .build();
    return ResponseEntity.ok(apiResponse);
  }

  @PutMapping(value = "/me/info", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<ApiResponse<TeamDetailResponse>> updateTeam(
          @CurrentUser User user,
          @RequestPart("data") UserUpdateRequest request,
          @RequestPart(value = "profileImage", required = false) MultipartFile image
  ) {
    User updated = userService.updateUser(user, request, image);
    UserDetailResponse res = UserDetailResponse.from(updated);
    ApiResponse apiResponse = ApiResponse.builder()
            .message("유저 정보 수정 성공")
            .status(OK.value())
            .data(res)
            .build();
    return ResponseEntity.ok(apiResponse);
  }

}
