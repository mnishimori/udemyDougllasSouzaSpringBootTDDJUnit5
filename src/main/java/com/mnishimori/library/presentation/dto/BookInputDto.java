package com.mnishimori.library.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record BookInputDto(@NotEmpty(message = "Informe o título") String title,
                           @NotEmpty(message = "Informe o autor") String author,
                           @NotEmpty(message = "Informe o isbn") String isbn) {

  public BookInputDto() {
    this("", "", "");
  }

  public BookInputDto(String title, String author, String isbn) {
    this.title = title;
    this.author = author;
    this.isbn = isbn;
  }

}
