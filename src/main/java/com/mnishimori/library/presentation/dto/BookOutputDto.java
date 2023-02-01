package com.mnishimori.library.presentation.dto;

public record BookOutputDto(Long id, String title, String author, String isbn) {
    public BookOutputDto(Long id, String title, String author, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }
}
