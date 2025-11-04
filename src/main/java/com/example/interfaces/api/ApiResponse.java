package com.example.interfaces.api;

public record ApiResponse<T>(Metadata metadata, T data) {

  public static ApiResponse<Object> success() {
    return new ApiResponse<>(Metadata.success(), null);
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(Metadata.success(), data);
  }
  
  public static ApiResponse<Object> fail(String errorCode, String errorMessage) {
    return new ApiResponse<>(Metadata.fail(errorCode, errorMessage), null);
  }

  public record Metadata(Status status, String errorCode, String message){

    public static Metadata success() {
      return new Metadata(Status.SUCCESS, null, null);
    }

    public static Metadata fail(String errorCode, String message) {
      return new Metadata(Status.FAIL, errorCode, message);
    }
    public enum Status {SUCCESS, FAIL}
  }
}
