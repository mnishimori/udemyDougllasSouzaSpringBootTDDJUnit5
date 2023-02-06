package com.mnishimori.library.domain.service;

import com.mnishimori.library.domain.model.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

  Book save(Book book);

  Optional<Book> findById(Long id);

  Book findByIdRequired(Long id);

  Book update(Book book);

  void delete(Long bookId);

  Page<Book> find(Book book, Pageable pageable);

  Object findByIsbn(String isbn);
}
