package com.dhbw.tutorsystem.tutorialOffer;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class TutorialOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private LocalDate start;

    @Getter
    @Setter
    private LocalDate end;

    @Getter
    @Setter
    private String appointments;

}