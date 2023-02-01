package com.mnishimori.library.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.repository.BookRepository;
import com.mnishimori.library.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

  private BookService service;

  @MockBean
  private BookRepository repository;

  @BeforeEach
  public void setUp() {
    this.service = new BookServiceImpl(repository);
  }

  @Test
  public void shouldSaveABook() {
    var book = createNewBook();
    when(service.save(book)).then(AdditionalAnswers.returnsFirstArg());

    Book savedBook = service.save(book);

    assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
  }

  @Test
  public void shouldNotCreateABookWhenIsbnIsDuplicated() throws Exception {
    var bookFound = createNewBook();
    bookFound.setId(1L);
    when(repository.findByIsbn(bookFound.getIsbn())).thenReturn(bookFound);

    var book = createNewBook();

    assertThatThrownBy(() -> service.save(book))
        .isInstanceOf(BusinessException.class)
        .hasMessage("ISBN já cadastrado");

    verify(repository, Mockito.never()).save(book);
  }

  private static Book createNewBook() {
    return Book.builder()
        .title("As aventuras")
        .author("Artur")
        .isbn("123456")
        .build();
  }
}