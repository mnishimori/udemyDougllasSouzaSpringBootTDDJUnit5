package com.mnishimori.library.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mnishimori.library.domain.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

  private final TestEntityManager entityManager;
  private final BookRepository repository;

  @Autowired
  public BookRepositoryTest(TestEntityManager testEntityManager, BookRepository repository) {
    this.entityManager = testEntityManager;
    this.repository = repository;
  }

  @Test
  void shouldReturnTrueWhenExistsABookWithIsbn(){
    var isbn = "123";
    var book = Book.builder().title("As aventuras").author("Artur").isbn(isbn).build();
    entityManager.persist(book);

    var isbnExists = repository.existsByIsbn(isbn);

    assertThat(isbnExists).isEqualTo(true);
  }
  @Test
  void shouldReturnFalseWhenDoesNotExistsABookWithIsbn(){
    var isbn = "123";

    var isbnExists = repository.existsByIsbn(isbn);

    assertThat(isbnExists).isEqualTo(false);
  }

  @Test
  void shouldReturnABookWhenExistsABookWithId(){
    var book = Book.builder().title("As aventuras").author("Artur").isbn("123456").build();
    entityManager.persist(book);

    var bookFound = repository.findById(book.getId());

    assertThat(bookFound.isPresent()).isEqualTo(true);
  }

  @Test
  void shouldSaveABook(){
    var book = Book.builder().title("As aventuras").author("Artur").isbn("123456").build();

    book = repository.save(book);

    assertThat(book.getId()).isNotNull();
  }

  @Test
  void shouldDeleteABook(){
    var book = Book.builder().title("As aventuras").author("Artur").isbn("123456").build();
    entityManager.persist(book);

    Book bookFound = entityManager.find(Book.class, book.getId());

    repository.delete(bookFound);

    Book bookDeleted = entityManager.find(Book.class, book.getId());
    assertThat(bookDeleted).isNull();
  }


}