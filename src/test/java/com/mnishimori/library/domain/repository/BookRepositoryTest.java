package com.mnishimori.library.domain.repository;

import com.mnishimori.library.domain.model.Book;
import org.assertj.core.api.Assertions;
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

    Assertions.assertThat(isbnExists).isEqualTo(true);
  }
  @Test
  void shouldReturnFalseWhenDoesNotExistsABookWithIsbn(){
    var isbn = "123";

    var isbnExists = repository.existsByIsbn(isbn);

    Assertions.assertThat(isbnExists).isEqualTo(false);
  }
}