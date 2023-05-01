package com.viksingh.authservice.dto.response;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public class ApiResponse {

  public static ResponseDTO response(@NonNull final HttpStatus status,
      @NonNull final String message,
      final Object data){
    return Response.getInstance()
        .createResponse(status, message, data);
  }

  public static ResponseDTO response(@NonNull final HttpStatus status,
      @NonNull final String message){
    return Response.getInstance()
        .createResponse(status, message);
  }

  public static ResponseDTO response(@NonNull final HttpStatus status,@NonNull final Object data){
    return Response.getInstance()
        .createResponse(status,data);
  }

  private static class Response extends ResponseDTO{
    private static Response response;

    private Response(){}

    public static Response getInstance() {
      if (response == null) {
        response = new Response();
      }
      return response;
    }

    public ResponseDTO createResponse(HttpStatus status,String message, Object data) {
      return ResponseDTO.builder()
          .status(status)
          .message(message)
          .data(data).build();
    }

    public ResponseDTO createResponse(HttpStatus status, String description) {
      return ResponseDTO.builder()
          .status(status)
          .build();
    }

    public ResponseDTO createResponse(HttpStatus status, Object data) {
      return ResponseDTO.builder()
          .status(status)
          .data(data)
          .build();
    }
  }
}
