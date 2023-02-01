package com.mnishimori.library.presentation.exceptionhandler;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiError {

  private Integer status_code;

  private String message;

  private List<String> errors;


  @Getter
  @Builder
  public static class Object {

    private String name;

    private String userMessage;
  }
}
