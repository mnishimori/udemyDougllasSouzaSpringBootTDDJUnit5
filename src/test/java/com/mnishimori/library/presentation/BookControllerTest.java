package com.mnishimori.library.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.exception.BusinessException;
import com.mnishimori.library.presentation.dto.BookInputDto;
import com.mnishimori.library.presentation.dto.BookMapper;
import java.util.Arrays;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

  static final String BOOK_API = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  BookService service;

  @Test
  public void shouldCreateABook() throws Exception {
    var bookInputDto = createNewBook();

    var savedBook = Book.builder()
        .id(1L)
        .author("Artur")
        .title("As aventuras")
        .isbn("123456")
        .build();

    BDDMockito
        .given(service.save(any(Book.class)))
        .willReturn(savedBook);

    var json = new ObjectMapper().writeValueAsString(bookInputDto);

    MockHttpServletRequestBuilder request = post(BOOK_API)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andDo(log())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(jsonPath("id").isNotEmpty())
        .andExpect(jsonPath("id").value(1L))
        .andExpect(jsonPath("title").value(bookInputDto.title()))
        .andExpect(jsonPath("author").value(bookInputDto.author()))
        .andExpect(jsonPath("isbn").value(bookInputDto.isbn()));
  }

  @Test
  public void shouldThrowAnExceptionWhenAnInvalidBookIsCreate() throws Exception {
    var bookInputDto = new BookInputDto();

    var json = new ObjectMapper().writeValueAsString(bookInputDto);

    MockHttpServletRequestBuilder request = post(BOOK_API)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void shouldNotCreateABookWhenIsbnIsDuplicated() throws Exception {
    var bookInputDto = createNewBook();

    var json = new ObjectMapper().writeValueAsString(bookInputDto);

    BDDMockito.given(service.save(any(Book.class)))
        .willThrow(new BusinessException("ISBN já cadastrado"));

    MockHttpServletRequestBuilder request = post(BOOK_API)
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void shouldGetDetailsFromBook() throws Exception {
    var book = new Book();
    book.setId(1L);

    BDDMockito.given(service.findById(book.getId())).willReturn(Optional.of(book));

    MockHttpServletRequestBuilder request = get(BOOK_API + "/" + book.getId())
        .accept(APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isOk());
  }

  @Test
  public void shouldThrownAnExceptionWhenGetABookNotFound() throws Exception {
    BDDMockito
        .given(service.findById(anyLong()))
        .willReturn(Optional.empty());

    MockHttpServletRequestBuilder request = get(BOOK_API + "/1")
        .accept(APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldDeleteABook() throws Exception {
    BDDMockito
        .given(service.findById(anyLong()))
        .willReturn(Optional.of(Book.builder().id(1L).build()));

    var request = delete(BOOK_API + "/1");

    mvc
        .perform(request)
        .andExpect(status().isNoContent());
  }

  @Test
  public void shouldReturnResourceNotFoundWhenDeleteABookNotFound() throws Exception {
    BDDMockito
        .given(service.findByIdRequired(anyLong()))
        .willThrow(BusinessException.class);

    var request = delete(BOOK_API + "/1");

    mvc
        .perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldUpdateABook() throws Exception {
    var id = 1l;
    var bookInputDto = createNewBook();
    var book = BookMapper.from(bookInputDto);
    var updatedBook = Book.builder().id(1L).title("Some title").author("Some author").isbn("123")
        .build();

    BDDMockito
        .given(service.findById(anyLong()))
        .willReturn(Optional.of(book));
    BDDMockito
        .given(service.save(book))
        .willReturn(updatedBook);

    var json = new ObjectMapper().writeValueAsString(bookInputDto);

    var request = put(BOOK_API + "/1")
        .content(json)
        .accept(APPLICATION_JSON)
        .contentType(APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(id))
        .andExpect(jsonPath("title").value(updatedBook.getTitle()))
        .andExpect(jsonPath("author").value(updatedBook.getAuthor()))
        .andExpect(jsonPath("isbn").value(updatedBook.getIsbn()));
  }

  @Test
  public void shouldReturnResouceNotFoundWhenUpdatedABookNotFound() throws Exception {
    var json = new ObjectMapper().writeValueAsString(createNewBook());
    BDDMockito
        .given(service.findById(anyLong()))
        .willReturn(
            Optional.empty());

    MockHttpServletRequestBuilder request = put(BOOK_API + "/1")
        .content(json)
        .accept(APPLICATION_JSON)
        .contentType(APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldFilterPageableBooks() throws Exception {
    var id = 1L;
    var book = Book.builder().id(id).title("Some title").author("Some author").isbn("123")
        .build();

    BDDMockito.given(service.find(any(Book.class), any(Pageable.class)))
        .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

    var queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(),
        book.getAuthor());
    var request = get(BOOK_API.concat(queryString))
        .accept(APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("content", Matchers.hasSize(1)))
        .andExpect(jsonPath("totalElements").value(1))
        .andExpect(jsonPath("pageable.pageSize").value(100))
        .andExpect(jsonPath("pageable.pageNumber").value(0));
  }

  private static BookInputDto createNewBook() {
    return new BookInputDto("As aventuras", "Artur", "123456");
  }

}
