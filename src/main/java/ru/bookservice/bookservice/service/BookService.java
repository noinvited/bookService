package ru.bookservice.bookservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookservice.bookservice.domain.Book;
import ru.bookservice.bookservice.domain.BookLibrary;
import ru.bookservice.bookservice.dto.BookResponse;
import ru.bookservice.bookservice.dto.LibraryResponse;
import ru.bookservice.bookservice.exception.BadBookRequestException;
import ru.bookservice.bookservice.repos.BookLibraryRepo;
import ru.bookservice.bookservice.repos.BookRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final BookLibraryRepo bookLibraryRepo;
    private final LibraryServiceClient libraryServiceClient;

    public BookResponse getBook(Long id) {
        Book book = getBookById(id);
        if (book == null) {
            return null;
        }
        List<BookLibrary> bookLibraries = getBookLibraryById(book);
        List<LibraryResponse> libraryResponses = new ArrayList<>();
        for(BookLibrary bookLibrary : bookLibraries) {
            libraryResponses.add(libraryServiceClient.getLibraryById(bookLibrary.getId()));
        }
        return new BookResponse(book.getTitle(), book.getAuthor(), libraryResponses);
    }

    public List<BookResponse> getBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        for(Book book : getAllBooks()) {
            List<BookLibrary> bookLibraries = getBookLibraryById(book);
            List<LibraryResponse> libraryResponses = new ArrayList<>();
            for(BookLibrary bookLibrary : bookLibraries) {
                libraryResponses.add(libraryServiceClient.getLibraryById(bookLibrary.getId()));
            }
            bookResponses.add(new BookResponse(book.getTitle(), book.getAuthor(), libraryResponses));
        }
        return bookResponses;
    }

    public Book getBookById(Long id) {
        return bookRepo.findById(id).orElseThrow(() -> new BadBookRequestException("Book not found", id));
    }

    public List<BookLibrary> getBookLibraryById(Book book) {
        return bookLibraryRepo.findBookLibrariesByBook(book);
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
}