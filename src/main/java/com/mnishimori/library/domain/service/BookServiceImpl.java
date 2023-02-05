package com.mnishimori.library.domain.service;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher.CONTAINING;
import static org.springframework.data.domain.ExampleMatcher.StringMatcher.STARTING;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.repository.BookRepository;
import com.mnishimori.library.exception.BusinessException;
import java.util.Optional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

  private BookRepository repository;

  public BookServiceImpl(BookRepository repository) {
    this.repository = repository;
  }

  @Override
  public Book save(Book book) {
    var bookFound = repository.findByIsbn(book.getIsbn());
    checkIfIsbnAlreadyExists(book, bookFound);
    return repository.save(book);
  }

  public Optional<Book> findById(Long id) {
    return repository.findById(id);
  }

  public Book findByIdRequired(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new BusinessException("Livro não encontrado"));
  }

  @Override
  public Book update(Book book) {
    if (book == null || book.getId() == null || book.getId() == 0L) {
      throw new IllegalArgumentException("Book id cant be null or zero");
    }
    var bookFound = findByIdRequired(book.getId());
    return repository.save(book);
  }

  @Override
  public void delete(Long bookId) {
    if (bookId == null || bookId == 0L) {
      throw new IllegalArgumentException("Book id cant be null or zero");
    }
    var book = findByIdRequired(bookId);
    repository.delete(book);
  }

  @Override
  public Page<Book> find(Book book, Pageable pageable) {
    var example = Example.of(book,
        ExampleMatcher.matching().withIgnoreCase().withIgnoreNullValues().withStringMatcher(
            CONTAINING));
    return repository.findAll(example, pageable);
  }

  private static void checkIfIsbnAlreadyExists(Book book, Book bookFound) {
    if (bookFound != null && !bookFound.getId().equals(book.getId())) {
      throw new BusinessException("ISBN já cadastrado");
    }
  }
}
