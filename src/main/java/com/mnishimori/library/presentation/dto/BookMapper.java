package com.mnishimori.library.presentation.dto;

import com.mnishimori.library.domain.model.Book;

import java.util.List;

public class BookMapper {

  public static Book from(BookInputDto bookInputDto) {
    return Book.builder()
        .title(bookInputDto.title())
        .author(bookInputDto.author())
        .isbn(bookInputDto.isbn())
        .build();
  }

  public static BookOutputDto to(Book book) {
    return new BookOutputDto(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn());
  }

  public static List<BookOutputDto> toList(List<Book> books) {
    return books.stream().map(BookMapper::to).toList();
  }
}
