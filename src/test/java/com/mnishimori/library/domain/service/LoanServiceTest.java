package com.mnishimori.library.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.model.Loan;
import com.mnishimori.library.domain.repository.LoanRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceTest {

  @MockBean
  private LoanRepository repository;
  private LoanService service;

  @BeforeEach
  void setUp(){
    service = new LoanServiceImpl(repository);
  }

  @Test
  void shouldSaveALoan(){
    var loan = createNewLoan();
    when(repository.save(loan)).then(AdditionalAnswers.returnsFirstArg());

    var loanSaved = service.save(loan);

    assertThat(loan).isSameAs(loanSaved);
  }

  private static Loan createNewLoan(){
    var book = createNewBook();
    return Loan.builder()
        .customer("JOSÉ")
        .book(book)
        .loanDate(LocalDate.now())
        .build();
  }

  private static Book createNewBook() {
    return Book.builder()
        .title("As aventuras")
        .author("Artur")
        .isbn("123456")
        .build();
  }
}