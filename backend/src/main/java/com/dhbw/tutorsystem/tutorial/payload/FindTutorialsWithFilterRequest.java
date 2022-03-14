package com.dhbw.tutorsystem.tutorial.payload;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FindTutorialsWithFilterRequest {

    private String text;

    private List<Integer> specialisationCourseIds;

    private LocalDate startDateFrom;

    private LocalDate startDateTo;

    private boolean selectParticipates;

    private boolean selectMarked;

    private boolean selectHolds;

}
