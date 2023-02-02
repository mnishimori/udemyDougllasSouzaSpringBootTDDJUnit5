package com.mnishimori.library.presentation;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.presentation.dto.BookInputDto;
import com.mnishimori.library.presentation.dto.BookMapper;
import com.mnishimori.library.presentation.dto.BookOutputDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private BookService service;

  public BookController(BookService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookOutputDto create(@RequestBody @Valid BookInputDto dto) {
    var book = BookMapper.from(dto);

    var savedBook = service.save(book);

    return BookMapper.to(savedBook);
  }

  @GetMapping("/{bookId}")
  public BookOutputDto getById(@PathVariable Long bookId){
    Book book = service.findById(Book.builder().id(1L).build());
    return BookMapper.to(book);
  }
}
