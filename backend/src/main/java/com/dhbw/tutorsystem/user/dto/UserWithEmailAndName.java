package com.dhbw.tutorsystem.user.dto;

import java.util.ArrayList;
import java.util.List;

import com.dhbw.tutorsystem.user.User;

import org.modelmapper.ModelMapper;

import lombok.Data;

@Data
public class UserWithEmailAndName {

    private String firstName;

    private String lastName;

    private String email;

    public static UserWithEmailAndName convertToDto(ModelMapper modelMapper, User user) {
        UserWithEmailAndName userWithEmailAndName = modelMapper.map(user, UserWithEmailAndName.class);
        return userWithEmailAndName;
    }

    public static List<UserWithEmailAndName> convertToDto(ModelMapper modelMapper, Iterable<User> users) {
        ArrayList<UserWithEmailAndName> coursesList = new ArrayList<>();
        for (User user : users) {
            coursesList.add(convertToDto(modelMapper, user));
        }
        return coursesList;
    }
}
