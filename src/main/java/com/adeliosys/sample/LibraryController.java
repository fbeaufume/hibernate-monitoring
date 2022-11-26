package com.adeliosys.sample;

import com.adeliosys.sample.model.Author;
import com.adeliosys.sample.model.Book;
import com.adeliosys.sample.repository.AuthorRepository;
import com.adeliosys.sample.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
public class LibraryController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostConstruct
    @GetMapping("/reset")
    public void reset() {
        authorRepository.deleteAll();
        authorRepository.saveAll(List.of(
                new Author("Author A").addBooks(
                        new Book("Book A1")),
                new Author("Author B").addBooks(
                        new Book("Book B1"),
                        new Book("Book B2"))));
    }

    @GetMapping("/list-authors")
    public List<Long> listAuthors() {
        return extractIds(authorRepository.findAll());
    }

    @GetMapping("/find-author")
    public List<Long> findAuthor(@RequestParam String name) {
        return extractIds(authorRepository.findByName(name));
    }

    public List<Long> extractIds(List<Author> authors) {
        return authors.stream().map(Author::getId).collect(Collectors.toList());
    }
}
