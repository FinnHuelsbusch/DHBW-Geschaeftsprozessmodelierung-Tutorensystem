package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Valid;

import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterRequest;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tutorials")
public class TutorialController {

        @Autowired
        private SpecialisationCourseRepository specialisationCourseRepository;

        @PersistenceUnit
        @Autowired
        private EntityManagerFactory entityManagerFactory;

        @Autowired
        ModelMapper modelMapper;

        @PostMapping("/findWithFilter")
        public ResponseEntity<FindTutorialsWithFilterResponse> findTutorialsWithFilter(Pageable pageable,
                        @Valid @RequestBody FindTutorialsWithFilterRequest filterRequest) {

                // construct filter conditions
                QTutorial tutorial = QTutorial.tutorial;

                // search text: any word matches title or description
                BooleanBuilder textMatches = new BooleanBuilder();
                if (filterRequest.getText() != null) {
                        List<String> textsToMatch = Arrays.asList(filterRequest.getText().split(" "));
                        for (String text : textsToMatch) {
                                text = "%" + text + "%";
                                textMatches.and(tutorial.title.likeIgnoreCase(text)
                                                .or(tutorial.description.likeIgnoreCase(text)));
                        }
                }

                // search specialisation course
                BooleanBuilder specialisationCourseMatches = new BooleanBuilder();
                if (filterRequest.getSpecialisationCourseIds() != null) {
                        Iterable<SpecialisationCourse> specialisationCourses = specialisationCourseRepository
                                        .findAllById((Iterable<Integer>) filterRequest.getSpecialisationCourseIds());
                        for (SpecialisationCourse specialisationCourse : specialisationCourses) {
                                specialisationCourseMatches
                                                .or(tutorial.specialisationCourses.contains(specialisationCourse));
                        }
                }

                // search start date within specified time range
                BooleanBuilder startsWithinTimeFrame = new BooleanBuilder();
                LocalDate defaultStartDateFrom = LocalDate.now().minusMonths(3);
                if (filterRequest.getStartDateFrom() == null &&
                                filterRequest.getStartDateTo() == null) {
                        // nothing specified, default to starting date 2 months in the past until
                        // infinity
                        startsWithinTimeFrame.and(tutorial.start.after(defaultStartDateFrom));
                } else if (filterRequest.getStartDateFrom() != null) {
                        // only from date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.after(filterRequest.getStartDateFrom()));
                } else if (filterRequest.getStartDateTo() != null) {
                        // only to date specified: default to starting date 2 months in the past until
                        // specified date
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(defaultStartDateFrom,
                                                        filterRequest.getStartDateTo()));
                } else {
                        // from and to date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(filterRequest.getStartDateFrom(),
                                                        filterRequest.getStartDateTo()));
                }

                // construct pageable details
                int start = (int) pageable.getOffset();
                int size = pageable.getPageSize();
                if (size <= 0) {
                        size = 4; // default to 4 in size
                }

                EntityManager entityManager = entityManagerFactory.createEntityManager();
                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
                entityManager.getTransaction().begin();

                // construct query from conditions, one for tutorial objects and one for total
                // element count
                JPAQuery<Tutorial> tutorialQuery = queryFactory
                                .selectFrom(tutorial)
                                .where(textMatches
                                                .and(specialisationCourseMatches)
                                                .and(startsWithinTimeFrame))
                                .offset(start)
                                .limit(size);

                JPAQuery<Long> countQuery = queryFactory
                                .from(tutorial)
                                .select(tutorial.id.countDistinct())
                                .where(textMatches
                                                .and(specialisationCourseMatches)
                                                .and(startsWithinTimeFrame));

                for (Sort.Order order : pageable.getSort()) {
                        com.querydsl.core.types.Order querydslOrder = order.isAscending()
                                        ? com.querydsl.core.types.Order.ASC
                                        : com.querydsl.core.types.Order.DESC;

                        if ("title".equals(order.getProperty())) {
                                tutorialQuery.orderBy(new OrderSpecifier<String>(
                                                querydslOrder,
                                                tutorial.title));
                        } else if ("start".equals(order.getProperty())) {
                                tutorialQuery.orderBy(new OrderSpecifier<LocalDate>(
                                                querydslOrder,
                                                tutorial.start));
                        }
                }

                List<Tutorial> filteredTutorials = tutorialQuery.fetch();
                int totalResultCount = countQuery.fetchFirst().intValue();

                for (Tutorial t : filteredTutorials) {
                        Hibernate.initialize(t.getSpecialisationCourses());
                        Hibernate.initialize(t.getTutors());
                        Hibernate.initialize(t.getParticipants());
                }

                entityManager.getTransaction().commit();
                entityManager.close();

                int totalPages = (int) Math.ceil((double) totalResultCount / pageable.getPageSize());

                // List<TutorialDto> tutorialDtos = filteredTutorials.stream().map(
                //                 t -> convertToDto(t)).collect(Collectors.toList());

                return ResponseEntity.ok(new FindTutorialsWithFilterResponse(
                                filteredTutorials,
                                pageable.getPageNumber(),
                                totalPages,
                                totalResultCount));
        }

        private TutorialDto convertToDto(Tutorial tutorial) {
                TutorialDto tutorialDto = modelMapper.map(tutorial, TutorialDto.class);
                tutorialDto.setTutorNames(tutorial.getTutors().stream().map(tutor -> tutor.getFirstName())
                                .collect(Collectors.toSet()));
                return tutorialDto;
        }

}
