package com.mnishimori.library.domain.repository;

import com.mnishimori.library.domain.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

  Book findByIsbn(String isbn);

  boolean existsByIsbn(String isbn);
}
