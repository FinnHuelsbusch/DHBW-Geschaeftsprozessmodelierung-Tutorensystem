package com.dhbw.tutorsystem.user.dto;

import java.util.ArrayList;
import java.util.List;

import com.dhbw.tutorsystem.user.User;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class UserWithEmailAndNameAndId{

    private int id; 

    private String firstName;

    private String lastName;

    private String email; 

    public static UserWithEmailAndNameAndId convertToDto(ModelMapper modelMapper, User user){
        UserWithEmailAndNameAndId userWithEmailAndNameAndId = modelMapper.map(user, UserWithEmailAndNameAndId.class); 
        return userWithEmailAndNameAndId;
    }

    public static List<UserWithEmailAndNameAndId> convertToDto(ModelMapper modelMapper, Iterable<User> users){
        ArrayList<UserWithEmailAndNameAndId> coursesList = new ArrayList<>(); 
        for (User user : users) {
            coursesList.add(convertToDto(modelMapper, user)); 
        }
        return coursesList;
    }
}
