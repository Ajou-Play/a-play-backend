package com.paran.aplay.common.error.exception;

import com.paran.aplay.common.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException{

  private final ErrorCode errorCode;

  public AlreadyExistsException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
