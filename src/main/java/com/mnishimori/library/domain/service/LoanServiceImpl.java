package com.mnishimori.library.domain.service;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.model.Loan;
import com.mnishimori.library.domain.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

  private final LoanRepository loanRepository;

  public LoanServiceImpl(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  @Override
  public Loan save(Loan loan) {
    return loanRepository.save(loan);
  }

  public boolean isBookAlreadyLoaned(Book book) {
    return loanRepository.findByBookAndReturned(book, false).isPresent();
  }
}
