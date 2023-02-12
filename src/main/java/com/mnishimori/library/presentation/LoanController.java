package com.mnishimori.library.presentation;

import com.mnishimori.library.domain.model.Loan;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.domain.service.LoanService;
import com.mnishimori.library.presentation.dto.LoanDto;
import com.mnishimori.library.presentation.dto.LoanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

  private LoanService loanService;
  private BookService bookService;
  private ModelMapper modelMapper;

  public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
    this.loanService = loanService;
    this.bookService = bookService;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Loan saveLoan(@RequestBody LoanDto loanDto){
    var book = bookService.findByIsbn(loanDto.getIsbn());
    var loan = LoanMapper.from(loanDto);
    loan.setBook(book.get());
    return loanService.save(loan);
  }

}
