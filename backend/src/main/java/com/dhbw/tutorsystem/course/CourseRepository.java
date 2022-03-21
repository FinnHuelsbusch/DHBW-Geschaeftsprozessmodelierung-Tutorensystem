package com.dhbw.tutorsystem.course;

import org.springframework.data.repository.CrudRepository;

// create a repository to interact with the DB by creating all CRUD operations
public interface CourseRepository extends CrudRepository<Course, Integer> {

}
