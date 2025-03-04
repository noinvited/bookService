package ru.bookservice.bookservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BookServiceApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    private static WireMockServer wireMockServer;

    private String bookByIdTestCorrectOnceCheck= "{\"title\":\"The Great Gatsby\",\"author\":\"F. Scott Fitzgerald\",\"libraries\":[{\"name\":\"№1\",\"location\":\"New York\"}]}";
    private String bookByIdTestCorrectSomeCheck= "{\"title\":\"The Hobbit\",\"author\":\"J.R.R. Tolkien\",\"libraries\":[{\"name\":\"№2\",\"location\":\"Los Angeles\"},{\"name\":\"№3\",\"location\":\"Chicago\"},{\"name\":\"№4\",\"location\":\"Houston\"}]}";
    private String bookByIdTestIncorrectCheck = "{\"message\":\"Book not found\",\"id\":20}";
    private String allBookTestCorrectCheck = "[{\"title\":\"The Great Gatsby\",\"author\":\"F. Scott Fitzgerald\",\"libraries\":[{\"name\":\"№1\",\"location\":\"New York\"}]},{\"title\":\"The Hobbit\",\"author\":\"J.R.R. Tolkien\",\"libraries\":[{\"name\":\"№2\",\"location\":\"Los Angeles\"},{\"name\":\"№3\",\"location\":\"Chicago\"},{\"name\":\"№4\",\"location\":\"Houston\"}]},{\"title\":\"The Lord of the Rings\",\"author\":\"J.R.R. Tolkien\",\"libraries\":[]}]";

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeAll
    static void setUp(@Value("${api.port}") int port) {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get("/libraries/1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"№1\",\"location\":\"New York\"}")));

        wireMockServer.stubFor(WireMock.get("/libraries/2")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"№2\",\"location\":\"Los Angeles\"}")));

        wireMockServer.stubFor(WireMock.get("/libraries/3")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"№3\",\"location\":\"Chicago\"}")));

        wireMockServer.stubFor(WireMock.get("/libraries/4")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"name\":\"№4\",\"location\":\"Houston\"}")));
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Информация о книге по id, с одной библиотекой")
    void GetBookByIdTestCorrectOnce() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books/1", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
        assertEquals(bookByIdTestCorrectOnceCheck, response.getBody());
    }

    @Test
    @DisplayName("Информация о книге по id, с несколькими библиотеками")
    void GetBookByIdTestCorrectSome() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books/2", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
        assertEquals(bookByIdTestCorrectSomeCheck, response.getBody());
    }

    @Test
    @DisplayName("Поиск несуществующей книги")
    void GetBookByIdTestIncorrect() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books/20", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
        assertEquals(bookByIdTestIncorrectCheck, response.getBody());
    }

    @Test
    @DisplayName("Информация о всех книгах")
    void GetAllBookTestCorrect() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
        assertEquals(allBookTestCorrectCheck, response.getBody());
    }
}
