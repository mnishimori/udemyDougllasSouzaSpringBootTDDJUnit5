package com.mnishimori.library.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.model.Loan;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.domain.service.LoanService;
import com.mnishimori.library.exception.BusinessException;
import com.mnishimori.library.presentation.dto.LoanDto;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

  private static final String LOAN_API = "/api/loans";

  @Autowired
  private MockMvc mvc;

  @MockBean
  private BookService bookService;
  @MockBean
  private LoanService loanService;

  @Test
  public void shouldCreateLoan() throws Exception {
    var loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();
    var json = new ObjectMapper().writeValueAsString(loanDto);
    var book = Book.builder().id(1L).isbn("123").build();
    var loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now())
        .build();

    BDDMockito
        .given(bookService.findByIsbn(loanDto.getIsbn()))
        .willReturn(Optional.of(book));
    BDDMockito
        .given(loanService.save(any(Loan.class)))
        .willReturn(loan);

    var request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("id").value(1L));
  }

  @Test
  public void shouldReturnExceptionWhenCreateAnInexistsBook() throws Exception {
    var loanDto = LoanDto.builder().isbn("123").customer("Fulano").build();
    var json = new ObjectMapper().writeValueAsString(loanDto);
    var book = Book.builder().id(1L).isbn("123").build();
    var loan = Loan.builder().id(1L).customer("Fulano").book(book).loanDate(LocalDate.now())
        .build();

    BDDMockito
        .given(bookService.findByIsbn(loanDto.getIsbn()))
        .willThrow(BusinessException.class);

    var request = MockMvcRequestBuilders.post(LOAN_API)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json);

    mvc.perform(request)
        .andExpect(status().isBadRequest());
  }
}
