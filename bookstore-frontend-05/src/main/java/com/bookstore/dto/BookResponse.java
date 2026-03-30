package com.bookstore.dto;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        Long authorId,
        String authorName,
        String isbn,
        BigDecimal price
) {
}
