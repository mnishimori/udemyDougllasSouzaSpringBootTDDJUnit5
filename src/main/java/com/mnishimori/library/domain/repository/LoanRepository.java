package com.mnishimori.library.domain.repository;

import com.mnishimori.library.domain.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

}
