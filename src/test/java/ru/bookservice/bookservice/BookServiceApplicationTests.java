package ru.bookservice.bookservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class LibraryServiceApplicationTests {
	@Autowired
	private MockMvc mockMvc;
	private static WireMockServer wireMockServer;

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
	static void setUp() {
		wireMockServer = new WireMockServer("${api.host}");
		wireMockServer.start();
	}

	@AfterAll
	static void tearDown() {
		wireMockServer.stop();
	}

	@BeforeEach
    void clear() {
		wireMockServer.resetAll();
	}

	@Test
	void GetBookByIdTestCorrectOnce() throws Exception {
		wireMockServer.stubFor(WireMock.get("/libraries")
				.withQueryParam("bookId", equalTo("1"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("{\"name\":\"№1\",\"location\":\"New York\"}")));

		mockMvc.perform(get("/books/1"))
				.andExpectAll(
						status().isOk(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						content().json("""
										{
											"title": "The Great Gatsby",
											"author": "F. Scott Fitzgerald",
											"listOfLibraries": [
												{
													"name": "№1",
													"location": "New York"
												}
											]
										}										
										""")
				);
	}

	/*@Test
	void GetBookByIdTestCorrectSome() throws Exception {
		mockMvc.perform(get("/books/2"))
				.andExpectAll(
						status().isOk(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						content().json("""
										{
										  	"title": "The Hobbit",
										  	"author": "J.R.R. Tolkien",
										  	"listOfLibraries": [
											 	 {
												  	"name": "№2",
												  	"location": "Los Angeles"
											  	},
											  	{
												  	"name": "№3",
												  	"location": "Chicago"
											  	},
											  	{
												 	 "name": "№4",
												  	"location": "Houston"
											 	 }
										  	]
									    }
										""")
				);
	}

	@Test
	void GetBookByIdTestIncorrect() throws Exception {
		mockMvc.perform(get("/books/20"))
				.andExpectAll(
						status().isBadRequest(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						content().json("""
										{
											"message": "Book not found",
											"id": 20
										}										
										""")
				);
	}

	@Test
	void GetAllBookTestCorrect() throws Exception {
		mockMvc.perform(get("/books"))
				.andExpectAll(
						status().isOk(),
						content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
						content().json("""
										[
											{
												"title": "The Great Gatsby",
												"author": "F. Scott Fitzgerald",
												"listOfLibraries": [
													{
														"name": "№1",
														"location": "New York"
													}
												]
											},
											{
												"title": "The Hobbit",
												"author": "J.R.R. Tolkien",
												"listOfLibraries": [
													{
														"name": "№2",
														"location": "Los Angeles"
													},
													{
														"name": "№3",
														"location": "Chicago"
													},
													{
														"name": "№4",
														"location": "Houston"
													}
												]
											},
											{
												"title": "The Lord of the Rings",
												"author": "J.R.R. Tolkien",
												"listOfLibraries": []
											}
										]
										""")
				);
	}*/
}
