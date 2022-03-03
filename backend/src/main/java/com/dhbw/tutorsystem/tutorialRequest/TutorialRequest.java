package com.dhbw.tutorsystem.tutorialRequest;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.dhbw.tutorsystem.module.Module;

import com.dhbw.tutorsystem.user.User;
import lombok.Getter;
import lombok.Setter;

@Entity
public class TutorialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String description;

    @ManyToOne(fetch =  FetchType.LAZY)
    @Getter
    @Setter
    private User user;

}
