package com.dhbw.tutorsystem.request;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dhbw.tutorsystem.module.Module;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "request")
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
