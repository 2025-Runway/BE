package com.example.runway.domain.search.service;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.search.dto.SearchCourseDto;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import com.example.runway.domain.search.error.NotFoundCourse;
import com.example.runway.domain.search.repository.SearchRepository;
import com.example.runway.domain.search.validator.SearchKeywordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    private final SearchKeywordValidator searchKeywordValidator;

    public List<SearchCoursesDto> coursesSearch(String q) {
        searchKeywordValidator.keywordExistValidate(q);
        List<Course> courses = searchRepository.findByCrsKorNmOrSigun(q);
        searchKeywordValidator.resultValidate(courses);
        return courses.stream().map(SearchCoursesDto::from).toList();
    }

    public SearchCourseDto courseSearch(String crsIdx) {
        Course c = searchRepository.findBycrsIdx(crsIdx)
                .orElseThrow(() -> NotFoundCourse.EXCEPTION);
        return SearchCourseDto.from(c);
    }
}
