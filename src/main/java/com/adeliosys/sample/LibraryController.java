package com.adeliosys.sample;

import com.adeliosys.sample.model.Author;
import com.adeliosys.sample.model.Book;
import com.adeliosys.sample.repository.AuthorRepository;
import com.adeliosys.sample.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
public class LibraryController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/reset")
    public void reset() {
        authorRepository.deleteAll();
        authorRepository.save(new Author("Author 1")
                .addBooks(new Book("Book 1"), new Book()));
    }
}
