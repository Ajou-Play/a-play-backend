package com.paran.aplay.common.error.exception;

import com.paran.aplay.common.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

  private final ErrorCode errorCode;

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}