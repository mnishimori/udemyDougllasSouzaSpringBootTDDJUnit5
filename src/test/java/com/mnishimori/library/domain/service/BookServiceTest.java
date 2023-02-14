package com.mnishimori.library.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.repository.BookRepository;
import com.mnishimori.library.exception.BusinessException;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    when(repository.save(book)).then(AdditionalAnswers.returnsFirstArg());

    var savedBook = service.save(book);

    assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
  }

  @Test
  public void shouldNotCreateABookWhenIsbnIsDuplicated() throws Exception {
    var book = createNewBook();
    book.setId(1L);
    var bookToSave = createNewBook();
    when(service.findByIsbn(bookToSave.getIsbn())).thenReturn(Optional.of(book));

    assertThatThrownBy(() -> service.checkIfIsbnAlreadyExists(bookToSave))
        .isInstanceOf(BusinessException.class)
        .hasMessage("ISBN já cadastrado");

    verify(repository, Mockito.never()).save(book);
  }

  @Test
  public void shouldFindABookById() {
    var book = createNewBook();
    book.setId(1L);
    when(repository.findById(book.getId()))
        .thenReturn(Optional.of(book));

    var bookFound = service.findById(book.getId());

    assertThat(bookFound.get()).isSameAs(book);
    assertThat(bookFound.isPresent()).isTrue();
    assertThat(bookFound.get().getId()).isEqualTo(book.getId());
    assertThat(bookFound.get().getAuthor()).isEqualTo(book.getAuthor());
    assertThat(bookFound.get().getTitle()).isEqualTo(book.getTitle());
    assertThat(bookFound.get().getIsbn()).isEqualTo(book.getIsbn());
    verify(repository).findById(book.getId());
  }

  @Test
  public void shouldNotFindABookByIdWhenIdNotExists() {
    var book = createNewBook();
    book.setId(1L);
    when(repository.findById(book.getId()))
        .thenReturn(Optional.empty());

    var bookFound = service.findById(book.getId());
    assertThat(bookFound.isEmpty()).isTrue();
    verify(repository).findById(book.getId());
  }

  @Test
  public void shouldFindABookByIsbn() {
    var book = createNewBook();
    book.setId(1L);
    when(repository.findByIsbn(book.getIsbn()))
        .thenReturn(Optional.of(book));

    var bookFound = service.findByIsbn(book.getIsbn());

    assertThat(bookFound.get()).isSameAs(book);
    verify(repository).findByIsbn(book.getIsbn());
  }

  @Test
  public void shouldNotFindABookByIsbnWhenIsbnNotExists() {
    var book = createNewBook();
    book.setId(1L);
    when(repository.findByIsbn(book.getIsbn()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findByIsbnRequired(book.getIsbn()))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Livro não encontrado");
  }

  @Test
  public void shouldUpdateAValidBook(){
    var book = createNewBook();
    book.setId(1L);
    book.setTitle("New title");
    book.setAuthor("New Author");
    book.setIsbn("99999");
    when(service.findById(book.getId())).thenReturn(Optional.of(book));
    when(service.update(book)).then(AdditionalAnswers.returnsFirstArg());

    var savedBook = service.update(book);

    assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
    verify(repository).save(book);
  }

  @Test
  public void shouldThrowExceptionWhenUpdateBookIdIsNull(){
    var book = createNewBook();
    book.setId(null);

    assertThatThrownBy(() -> service.update(book))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book id cant be null or zero");
  }

  @Test
  public void shouldThrowExceptionWhenUpdateBookIdIsZero(){
    var book = createNewBook();
    book.setId(0L);

    assertThatThrownBy(() -> service.update(book))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book id cant be null or zero");
  }

  @Test
  public void shouldThrowExceptionWhenUpdateBookNotFound(){
    var book = createNewBook();
    book.setId(1L);

    assertThatThrownBy(() -> service.update(book))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Livro não encontrado");
  }

  @Test
  public void shouldDeleteABook() {
    var bookId = 1L;
    var book = createNewBook();
    book.setId(bookId);

    when(repository.findById(bookId)).thenReturn(Optional.of(book));

    assertThatCode(() -> service.delete(bookId)).doesNotThrowAnyException();
    verify(repository).delete(book);
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWhenBookIdIsnull() {
    Long bookId = null;

    assertThatThrownBy(() -> service.delete(bookId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book id cant be null or zero");
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWhenBookIdIsZero() {
    var bookId = 0L;

    assertThatThrownBy(() -> service.delete(bookId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Book id cant be null or zero");
  }

  @Test
  public void shouldThrowABusinessExceptionWhenDeleteInexistentBook() {
    var bookId = 1L;

    assertThatThrownBy(() -> service.delete(bookId))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Livro não encontrado");
  }

  @Test
  public void shouldFindBookByProperties(){
    var book = createNewBook();
    var books = Arrays.asList(book);
    var pageRequest = PageRequest.of(0,10);
    Page<Book> page = new PageImpl<Book>(books, pageRequest, 1);
    when(repository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

    Page<Book> results = service.find(book, pageRequest);

    assertThat(results.getTotalElements()).isEqualTo(1);
    assertThat(results.getContent()).isEqualTo(books);
    assertThat(results.getPageable().getPageNumber()).isEqualTo(0);
    assertThat(results.getPageable().getPageSize()).isEqualTo(10);
  }


  private static Book createNewBook() {
    return Book.builder()
        .title("As aventuras")
        .author("Artur")
        .isbn("123456")
        .build();
  }
}