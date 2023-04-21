package com.mnishimori.library.domain.repository;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.model.Loan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

  Optional<Loan> findByBookAndReturned(Book book, boolean returned);
}
