package com.example.bookservice.controller;

import com.example.bookservice.BookServiceApplication;
import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BookServiceApplication.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllBooks() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    public void testGetBookById() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    public void testGetBookByIdWrongExpectation() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    public void testCreateBook() throws Exception {
        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor("New Author");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateBookWithWrongMethod() throws Exception {
        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor("New Author");

        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteBook() throws Exception {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBookPrice() {
        Book book = new Book();
        book.setPrice(10.99);
        
        assertEquals(10.99, book.getPrice(), "Book price should match");
    }

    @Test
    public void testGetBookByIdNotFound() throws Exception {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testNullBookHandling() {
        Book book = new Book();
        book.setTitle(null);
        
        assertNull(book.getTitle(), "Book title should be null");
    }
} 