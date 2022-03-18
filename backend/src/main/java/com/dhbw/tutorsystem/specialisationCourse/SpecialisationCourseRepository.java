package com.dhbw.tutorsystem.specialisationCourse;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

public interface SpecialisationCourseRepository extends CrudRepository<SpecialisationCourse, Integer> {
    Set<SpecialisationCourse> findAllById(Iterable<Integer> ids);

    boolean existsByIdIn(Set<Integer> ids);
}
