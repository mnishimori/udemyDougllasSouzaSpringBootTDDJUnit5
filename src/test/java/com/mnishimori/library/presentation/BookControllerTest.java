package com.mnishimori.library.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.service.BookService;
import com.mnishimori.library.exception.BusinessException;
import com.mnishimori.library.presentation.dto.BookInputDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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
        .given(service.save(Mockito.any(Book.class)))
        .willReturn(savedBook);

    String json = new ObjectMapper().writeValueAsString(bookInputDto);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
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

    String json = new ObjectMapper().writeValueAsString(bookInputDto);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void shouldNotCreateABookWhenIsbnIsDuplicated() throws Exception {
    var bookInputDto = createNewBook();

    String json = new ObjectMapper().writeValueAsString(bookInputDto);

    BDDMockito.given(service.save(Mockito.any(Book.class)))
        .willThrow(new BusinessException("ISBN já cadastrado"));

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .post(BOOK_API)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

    mvc
        .perform(request)
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void shouldGetDetailsFromBook() throws Exception {
    var book = new Book();
    book.setId(1L);

    BDDMockito.given(service.findById(book)).willReturn(book);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
        .get(BOOK_API + "/" + book.getId())
        .accept(MediaType.APPLICATION_JSON);

    mvc
        .perform(request)
        .andExpect(status().isOk());
  }

  private static BookInputDto createNewBook() {
    return new BookInputDto("As aventuras", "Artur", "123456");
  }

}
