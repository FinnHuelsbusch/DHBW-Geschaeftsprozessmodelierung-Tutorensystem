package com.dhbw.tutorsystem.tutorial;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.validation.Valid;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.PagedList;
import com.blazebit.persistence.querydsl.BlazeCriteriaBuilderRenderer;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourse;
import com.dhbw.tutorsystem.specialisationCourse.SpecialisationCourseRepository;
import com.dhbw.tutorsystem.tutorial.payload.FindTutorialsWithFilterRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

        @PostMapping("/findWithFilter")
        public ResponseEntity<?> findTutorialsWithFilter(Pageable pageable,
                        @Valid @RequestBody FindTutorialsWithFilterRequest filterRequest) {

                // construct filter conditions
                QTutorial tutorial = QTutorial.tutorial;

                // search text: any word matches title or description
                List<String> textsToMatch = Arrays.asList(filterRequest.getTitle().split(""));
                BooleanBuilder textMatches = new BooleanBuilder();
                for (String text : textsToMatch) {
                        text = "%" + text + "%";
                        textMatches.and(
                                        tutorial.title.likeIgnoreCase(text)
                                                        .or(tutorial.description.likeIgnoreCase(text)));
                }

                // search specialisation course
                Iterable<SpecialisationCourse> specialisationCourses = specialisationCourseRepository
                                .findAllById((Iterable<Integer>) filterRequest.getSpecialisationCourseIds().iterator());
                BooleanBuilder specialisationCourseMatches = new BooleanBuilder();
                for (SpecialisationCourse specialisationCourse : specialisationCourses) {
                        specialisationCourseMatches.or(tutorial.specialisationCourses.contains(specialisationCourse));
                }

                // search start date within specified time range
                BooleanBuilder startsWithinTimeFrame = new BooleanBuilder();
                if (filterRequest.getStartDateFrom() == null &&
                                filterRequest.getStartDateTo() == null) {
                        // nothing specified, default to starting date 2 months in the past until
                        // infinity
                        startsWithinTimeFrame.and(
                                        tutorial.start.after(LocalDate.now().minusMonths(2)));
                } else if (filterRequest.getStartDateFrom() == null) {
                        // only from date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.after(filterRequest.getStartDateFrom()));
                } else if (filterRequest.getStartDateTo() == null) {
                        // only to date specified: default to starting date 2 months in the past until
                        // specified date
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(LocalDate.now().minusMonths(2),
                                                        filterRequest.getStartDateTo()));
                } else {
                        // from and to date specified
                        startsWithinTimeFrame.and(
                                        tutorial.start.between(filterRequest.getStartDateFrom(),
                                                        filterRequest.getStartDateTo()));
                }

                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();
                JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

                // construct pageable details
                int start = (int) pageable.getOffset();
                int size = pageable.getPageSize();
                if (size <= 0) {
                        size = 4; // default to 4 in size
                }

                // construct query from conditions
                // BlazeJPAQuery
                new BlazeJPAQuery<>(entityManage)
                BlazeJPAQuery<Tutorial> query = queryFactory
                                .selectFrom(tutorial)
                                .where(
                                                textMatches
                                                                .and(specialisationCourseMatches)
                                                                .and(startsWithinTimeFrame))
                                .offset(start)
                                .limit(size);

                QueryResults<Tutorial> tQueryResults = query.fetchResults();

                // List<Tutorial> tutorials = query.fetchResults();
                // CriteriaBuilderConfiguration config = Criteria.getDefault();
                // CriteriaBuilderFactory cbf = config.createCriteriaBuilderFactory(entityManagerFactory);
                // PagedList<Tutorial> page = cbf.create(
                //                 entityManager, Tutorial.class, "t")
                //                 // .where("t.start").between(LocalDate.now()).and(LocalDate.now().plusDays(2))
                //                 .where("t.title").like().value(filterRequest.getTitle()).noEscape()
                //                 .where("t.specialisationCourses")
                //                 .inCollectionExpression("cat.age IN (1L, 2L, 3L, :param)")
                //                 .orderBy("t.id", true)
                //                 .page(0, 3)
                //                 .getResultList();

                return ResponseEntity.ok(null);
        }

}
