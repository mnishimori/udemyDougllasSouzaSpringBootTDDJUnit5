package com.mnishimori.library.presentation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoanDto {

  private String isbn;
  private String customer;

}
