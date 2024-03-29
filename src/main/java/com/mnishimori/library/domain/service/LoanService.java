package com.mnishimori.library.domain.service;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.model.Loan;

public interface LoanService {

  Loan save(Loan loan);

  boolean isBookAlreadyLoaned(Book book);
}
