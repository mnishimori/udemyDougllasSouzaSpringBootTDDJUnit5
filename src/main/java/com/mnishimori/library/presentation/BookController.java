package com.mnishimori.library.presentation;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.exception.BusinessException;
import com.mnishimori.library.presentation.dto.BookInputDto;
import com.mnishimori.library.presentation.dto.BookMapper;
import com.mnishimori.library.presentation.dto.BookOutputDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private BookService service;

  @Autowired
  private ModelMapper modelMapper;

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
  public BookOutputDto getById(@PathVariable Long bookId) {
    var book = getBook(bookId);
    return BookMapper.to(book);
  }

  @GetMapping
  public Page<BookOutputDto> find(BookInputDto bookInputDto, Pageable pageable) {
    var book = BookMapper.from(bookInputDto);
    Page<Book> result = service.find(book, pageable);
    var books = result.getContent()
        .stream()
        .map(bookFound -> BookMapper.to(bookFound))
        .collect(Collectors.toList());
    return new PageImpl<>(books, pageable, result.getTotalElements());
  }

  @PutMapping("/{bookId}")
  @ResponseStatus(HttpStatus.OK)
  public BookOutputDto update(@PathVariable Long bookId, @RequestBody BookInputDto bookInputDto) {
    var book = getBook(bookId);
    modelMapper.map(bookInputDto, book);
    book = service.save(book);
    return BookMapper.to(book);
  }

  @DeleteMapping("/{bookId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long bookId) {
    var book = getBook(bookId);
    service.delete(book.getId());
  }


  private Book getBook(Long bookId) {
    return service.findById(bookId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}
