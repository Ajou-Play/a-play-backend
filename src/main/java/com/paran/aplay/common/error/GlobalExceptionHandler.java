package com.paran.aplay.common.error;

import com.paran.aplay.common.ErrorCode;
import com.paran.aplay.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  // 500 : Internal Server Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleServerException(Exception e) {
    return handleException(e, ErrorCode.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ErrorResponse> handleException(Exception e, ErrorCode errorCode) {
    log.warn(e.getMessage(), e);
    ErrorResponse errorResponse = ErrorResponse.of(errorCode);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }
}
