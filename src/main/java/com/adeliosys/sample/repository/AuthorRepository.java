package com.adeliosys.sample.repository;

import com.adeliosys.sample.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
