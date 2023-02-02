package com.mnishimori.library.domain.service;

import com.mnishimori.library.domain.model.Book;
import com.mnishimori.library.domain.repository.BookRepository;
import com.mnishimori.library.exception.BusinessException;
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

    @Override
    public Book findById(Book book) {
        return repository.findById(book.getId())
            .orElseThrow(() -> new BusinessException("Livro não encontrado!"));
    }

    private static void checkIfIsbnAlreadyExists(Book book, Book bookFound) {
        if (bookFound != null && !bookFound.getId().equals(book.getId())){
            throw new BusinessException("ISBN já cadastrado");
        }
    }
}
