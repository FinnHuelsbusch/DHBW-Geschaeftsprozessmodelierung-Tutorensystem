package com.dhbw.tutorsystem.module;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

public class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String title;    
}
