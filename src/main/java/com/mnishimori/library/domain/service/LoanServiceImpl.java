package com.mnishimori.library.domain.service;

import com.mnishimori.library.domain.model.Loan;
import com.mnishimori.library.domain.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService{

  private LoanRepository loanRepository;

  public LoanServiceImpl(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  @Override
  @Transactional
  public Loan save(Loan loan) {
    return loanRepository.save(loan);
  }
}
