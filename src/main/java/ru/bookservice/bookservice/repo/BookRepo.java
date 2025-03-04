package ru.bookservice.bookservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookservice.bookservice.domain.Book;

public interface BookRepo extends JpaRepository<Book, Long> {
}