package ru.bookservice.bookservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookservice.bookservice.client.LibraryGateway;
import ru.bookservice.bookservice.domain.Book;
import ru.bookservice.bookservice.domain.BookLibrary;
import ru.bookservice.bookservice.dto.BookResponse;
import ru.bookservice.bookservice.dto.LibraryResponse;
import ru.bookservice.bookservice.exception.BadBookRequestException;
import ru.bookservice.bookservice.exception.ServerNotAvailableException;
import ru.bookservice.bookservice.repo.BookLibraryRepo;
import ru.bookservice.bookservice.repo.BookRepo;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final BookLibraryRepo bookLibraryRepo;
    private final LibraryGateway libraryGateway;

    /**
     * Возвращает информацию о книге по её идентификатору, включая список библиотек, в которых она находится.
     *
     * @param id идентификатор книги
     * @return объект {@link BookResponse}, содержащий название, автора и список библиотек
     * @throws BadBookRequestException если книга с указанным идентификатором не найдена
     * @throws ServerNotAvailableException если сервер внешнего клиента временно недоступен или произошла ошибка при подключении
     */
    public BookResponse getBook(Long id) throws BadBookRequestException, ServerNotAvailableException {
        Book book = getBookById(id);
        List<BookLibrary> bookLibraries = getBookLibraryById(book);
        List<LibraryResponse> libraryResponses = new ArrayList<>();
        for(BookLibrary bookLibrary : bookLibraries) {
            libraryResponses.add(libraryGateway.getLibraryById(bookLibrary.getId()));
        }
        return new BookResponse(book.getTitle(), book.getAuthor(), libraryResponses);
    }

    /**
     * Возвращает список всех книг с информацией о библиотеках, в которых они находятся.
     *
     * @return список объектов {@link BookResponse}, содержащих название, автора и список библиотек для каждой книги
     *
     * @throws ServerNotAvailableException если сервер временно недоступен или произошла ошибка при подключении
     */
    public List<BookResponse> getBooks() throws ServerNotAvailableException{
        List<BookResponse> bookResponses = new ArrayList<>();
        for(Book book : getAllBooks()) {
            List<BookLibrary> bookLibraries = getBookLibraryById(book);
            List<LibraryResponse> libraryResponses = new ArrayList<>();
            for(BookLibrary bookLibrary : bookLibraries) {
                libraryResponses.add(libraryGateway.getLibraryById(bookLibrary.getId()));
            }
            bookResponses.add(new BookResponse(book.getTitle(), book.getAuthor(), libraryResponses));
        }
        return bookResponses;
    }

    /**
     * Возвращает объект книги из базы данных по её идентификатору.
     *
     * @param id идентификатор книги
     * @return объект {@link Book}, представляющий книгу
     * @throws BadBookRequestException если книга с указанным идентификатором не найдена
     */
    public Book getBookById(Long id) throws BadBookRequestException{
        return bookRepo.findById(id).orElseThrow(() -> new BadBookRequestException("Book not found", id));
    }

    /**
     * Возвращает список библиотек из базы данных, в которых находится данная книга.
     *
     * @param book объект {@link Book}, для которого требуется найти связи с библиотеками
     * @return список объектов {@link BookLibrary}, представляющих связи книги с библиотеками
     */
    public List<BookLibrary> getBookLibraryById(Book book) {
        return bookLibraryRepo.findBookLibrariesByBook(book);
    }

    /**
     * Возвращает список всех книг из баз данных.
     *
     * @return список объектов {@link Book}, представляющих все книги
     */
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
}