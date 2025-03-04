package ru.bookservice.bookservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookservice.bookservice.domain.Book;
import ru.bookservice.bookservice.domain.BookLibrary;

import java.util.List;

public interface BookLibraryRepo extends JpaRepository<BookLibrary, Long> {
    List<BookLibrary> findBookLibrariesByBook(Book book);
}
