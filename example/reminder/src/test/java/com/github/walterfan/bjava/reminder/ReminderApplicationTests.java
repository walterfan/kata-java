package com.github.walterfan.bjava.reminder;

import com.github.walterfan.bjava.reminder.domain.Book;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReminderApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", new Date());
		Book expectedBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = new Book("1231231231", "Title", "Author", new Date());

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", new Date());
		Book createdBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();
		var bookToUpdate = new Book(createdBook.isbn(), createdBook.title(), createdBook.author(), new Date());

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.borrowTime()).isEqualTo(bookToUpdate.borrowTime());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = new Book(bookIsbn, "Title", "Author", new Date());
		webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage ->
						assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
				);
	}

}
