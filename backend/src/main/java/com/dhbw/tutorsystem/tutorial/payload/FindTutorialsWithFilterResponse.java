package com.dhbw.tutorsystem.tutorial.payload;

import java.util.List;

import com.dhbw.tutorsystem.tutorial.Tutorial;

import lombok.Getter;

public class FindTutorialsWithFilterResponse extends PagedDataResponse {

    @Getter
    private List<Tutorial> tutorials;

    public FindTutorialsWithFilterResponse(List<Tutorial> tutorials, int currentPage, int totalPages,
            long totalElements) {
        super(currentPage, totalPages, totalElements);
        this.tutorials = tutorials;
    }

}
