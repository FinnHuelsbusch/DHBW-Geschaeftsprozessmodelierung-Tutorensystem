package com.dhbw.tutorsystem.request;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Module module;

}
