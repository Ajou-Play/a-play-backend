package com.paran.aplay.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // Server Error
  INTERNAL_SERVER_ERROR(500, "S000", "서버에 문제가 생겼습니다."),

  // Client Error
  METHOD_NOT_ALLOWED(405, "C000", "적절하지 않은 HTTP 메소드입니다."),
  INVALID_TYPE_VALUE(400, "C001", "요청 값의 타입이 잘못되었습니다."),
  INVALID_INPUT_VALUE(400, "C002", "적절하지 않은 값입니다."),
  NOT_FOUND(404, "C003", "해당 리소스를 찾을 수 없습니다."),
  BAD_REQUEST(400, "C004", "잘못된 요청입니다."),
  MISSING_REQUEST_PARAMETER(400, "C005", "필수 파라미터가 누락되었습니다."),
  INVALID_LENGTH(400, "C006", "올바르지 않은 길이입니다."),
  INVALID_FILE_EXTENSION(400, "C007", "올바르지 않은 파일 확장자입니다. (png, jpg, jpeg 가능)"),
  MAX_UPLOAD_SIZE_EXCEEDED(400, "C008", "최대 파일 크기(5MB)보다 큰 파일입니다."),
  RESOURCE_PERMISSION_DENIED(400, "C009", "해당 리소스에 대한 작업 권한이 없습니다."),
  ACCESS_DENIED(403, "C010", "요청 권한이 없습니다."),
  UNAUTHENTICATED_USER(401, "C011", "인증되지 않은 사용자입니다."),

  /**
   * User Domain
   */
  USER_NOT_FOUND(400, "U001", "유저가 존재하지 않습니다."),
  INVALID_ACCOUNT_REQUEST(400, "U002", "아이디 및 비밀번호가 올바르지 않습니다."),
  INVALID_REFRESH_TOKEN_REQUEST(400, "U003", "토큰이 올바르지 않습니다."),
  USER_ALREADY_EXISTS(400, "U004", "유저가 이미 존재합니다."),
  TOKEN_EXPIRED(400, "U005", "토큰이 만료되었습니다."),
  LOGIN_PARAM_REQUIRED(400, "U006", "로그인 파라미터가 누락되었습니다."),
  ACCESS_TOKEN_REQUIRED(400, "U007", "access token은 필수입니다."),
  EMAIL_REQUIRED(400, "U008", "이메일은 필수입니다."),
  ROLE_NOT_FOUND(400, "U009", "역할이 존재하지 않습니다."),
  USER_PARAM_REQUIRED(400, "U010", "유저가 누락되었습니다."),
  USER_PROFILE_NOT_MATCHED(400, "U011", "수정할 프로필 사진이 누락되었으며, 유저의 기존 프로필 이미지와 다른 링크입니다."),
  NICKNAME_ALREADY_EXISTS(400, "U012", "이미 존재하는 닉네임입니다."),
  TOKEN_NOT_EXPIRED(400, "U013", "토큰이 아직 만료되지 않았으므로 재발행할 수 없습니다."),
  PASSWORD_CANNOT_BE_SAME(400, "U014", "새 비밀번호는 이전 비밀번호와 같을 수 없습니다."),
  REDIS_TOKEN_NOT_FOUND(500, "U015", "유저에 해당하는 토큰을 찾을 수 없습니다."),
  TOKEN_USER_ID_NOT_MATCHED(400, "U016", "액세스 토큰과 유저 아이디가 매치되지 않습니다."),
  BLACKLIST_TOKEN_REQUEST(400, "U017", "로그아웃 처리된 토큰으로 요청할 수 없습니다."),
  OAUTH_PROVIDER_UNSUPPORTED(500, "U018", "아직 지원되지 않은 소셜로그인입니다."),

  OAUTH_EMAIL_REQUIRED(500, "U019", "OAuth email을 수집하는데 실패하였습니다.");
  private final int status;
  private final String code;
  private final String message;

  ErrorCode(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

}
