package com.example.interfaces.api;

import com.example.common.error.CoreException;
import com.example.common.error.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

  @ExceptionHandler
  public ResponseEntity<ApiResponse<?>> handle(CoreException e) {
    return this.failureResponse(e.getErrorType(), e.getCustomMessage());
  }

  private ResponseEntity<ApiResponse<?>> failureResponse(ErrorType errorType, String errorMessage) {
    return ResponseEntity.status(errorType.getStatus())
        .body(ApiResponse.fail(errorType.getCode(), errorMessage != null ? errorMessage : errorType.getMessage()));
  }

}
