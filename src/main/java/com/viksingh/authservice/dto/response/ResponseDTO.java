package com.viksingh.authservice.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDTO implements Serializable {
  private HttpStatus status;
  private String message;
  private Object data;
}
