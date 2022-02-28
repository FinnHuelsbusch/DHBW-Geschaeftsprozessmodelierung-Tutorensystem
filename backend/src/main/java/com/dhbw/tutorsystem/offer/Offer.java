package com.dhbw.tutorsystem.offer;
import java.time.LocalDate;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

public class Offer {

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
