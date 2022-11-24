package com.adeliosys.sample.repository;

import com.adeliosys.sample.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
