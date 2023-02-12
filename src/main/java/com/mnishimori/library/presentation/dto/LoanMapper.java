package com.mnishimori.library.presentation.dto;

import com.mnishimori.library.domain.model.Loan;

public class LoanMapper {

  public static Loan from(LoanDto loanDto) {
    return Loan.builder()
        .customer(loanDto.getCustomer())
        .build();
  }
}
