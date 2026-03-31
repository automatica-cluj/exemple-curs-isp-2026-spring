package com.bookstore.controller;

import com.bookstore.dto.BookResponse;
import com.bookstore.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@DisplayName("BookController")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    private static final String VALID_BOOK_JSON = """
            {
                "title": "Clean Code",
                "authorId": 1,
                "isbn": "9780132350884",
                "price": 39.99
            }
            """;

    @Nested
    @DisplayName("GET /api/books")
    class GetAllBooks {

        @Test
        @DisplayName("returns 200 with JSON array")
        void returns200WithJsonArray() throws Exception {
            when(bookService.findAll()).thenReturn(List.of(
                    new BookResponse(1L, "Clean Code", 1L, "Robert Martin", "9780132350884", new BigDecimal("39.99")),
                    new BookResponse(2L, "Effective Java", 2L, "Joshua Bloch", "9780134685991", new BigDecimal("45.00"))
            ));

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Clean Code"))
                    .andExpect(jsonPath("$[1].title").value("Effective Java"));
        }
    }

    @Nested
    @DisplayName("GET /api/books/{id}")
    class GetBookById {

        @Test
        @DisplayName("returns 200 with JSON object")
        void returns200WithJsonObject() throws Exception {
            when(bookService.findById(1L)).thenReturn(
                    new BookResponse(1L, "Clean Code", 1L, "Robert Martin", "9780132350884", new BigDecimal("39.99"))
            );

            mockMvc.perform(get("/api/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Clean Code"))
                    .andExpect(jsonPath("$.authorName").value("Robert Martin"));
        }

        @Test
        @DisplayName("returns 404 when book not found")
        void returns404WhenBookNotFound() throws Exception {
            when(bookService.findById(999L))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: 999"));

            mockMvc.perform(get("/api/books/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/books")
    class CreateBook {

        @Test
        @DisplayName("returns 201 with valid body")
        void returns201WithValidBody() throws Exception {
            when(bookService.create(any())).thenReturn(
                    new BookResponse(1L, "Clean Code", 1L, "Robert Martin", "9780132350884", new BigDecimal("39.99"))
            );

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BOOK_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Clean Code"));
        }

        @Test
        @DisplayName("returns 400 with blank title")
        void returns400WithBlankTitle() throws Exception {
            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "", "authorId": 1, "isbn": "9780132350884", "price": 39.99}
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 with null authorId")
        void returns400WithNullAuthorId() throws Exception {
            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Clean Code", "isbn": "9780132350884", "price": 39.99}
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 with negative price")
        void returns400WithNegativePrice() throws Exception {
            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Clean Code", "authorId": 1, "isbn": "9780132350884", "price": -5.00}
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 409 for duplicate ISBN")
        void returns409ForDuplicateIsbn() throws Exception {
            when(bookService.create(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "A book with ISBN 9780132350884 already exists"));

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BOOK_JSON))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("PUT /api/books/{id}")
    class UpdateBook {

        @Test
        @DisplayName("returns 200 with valid body")
        void returns200WithValidBody() throws Exception {
            when(bookService.update(eq(1L), any())).thenReturn(
                    new BookResponse(1L, "Clean Code", 1L, "Robert Martin", "9780132350884", new BigDecimal("39.99"))
            );

            mockMvc.perform(put("/api/books/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BOOK_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("returns 404 when book not found")
        void returns404WhenBookNotFound() throws Exception {
            when(bookService.update(eq(999L), any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: 999"));

            mockMvc.perform(put("/api/books/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BOOK_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/books/{id}")
    class DeleteBook {

        @Test
        @DisplayName("returns 204 for existing book")
        void returns204ForExistingBook() throws Exception {
            doNothing().when(bookService).delete(1L);

            mockMvc.perform(delete("/api/books/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 404 when book not found")
        void returns404WhenBookNotFound() throws Exception {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found with id: 999"))
                    .when(bookService).delete(999L);

            mockMvc.perform(delete("/api/books/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
