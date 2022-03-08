package com.dhbw.tutorsystem.tutorial.payload;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class PagedDataResponse {

    private int currentPage;

    private int totalPages;

    private long totalElements;

}
