package com.dhbw.tutorsystem.tutorial.payload;

import java.util.List;

import com.dhbw.tutorsystem.tutorial.Tutorial;
import com.dhbw.tutorsystem.tutorial.TutorialDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FindTutorialsWithFilterResponse {

    private List<TutorialDto> tutorials;

    private int currentPage;

    private int totalPages;

    private int totalElements;

}
