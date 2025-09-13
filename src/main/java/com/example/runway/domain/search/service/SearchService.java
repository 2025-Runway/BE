package com.example.runway.domain.search.service;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.search.dto.SearchCourseDto;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import com.example.runway.domain.search.error.NotFoundCourse;
import com.example.runway.domain.search.repository.KeywordRepository;
import com.example.runway.domain.search.repository.SearchRepository;
import com.example.runway.domain.search.validator.SearchKeywordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    private final SearchKeywordValidator searchKeywordValidator;
    private final KeywordRepository keywordRepository;

    public Page<SearchCoursesDto> coursesSearch(String q, int page) {
        searchKeywordValidator.keywordExistValidate(q);
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Course> courses = searchRepository.findByCrsKorNmOrSigun(q, pageRequest);
        searchKeywordValidator.resultValidate(courses);
        return courses.map(SearchCoursesDto::from);
    }

    public SearchCourseDto courseSearch(String crsIdx) {
        Course c = searchRepository.findBycrsIdx(crsIdx)
                .orElseThrow(() -> NotFoundCourse.EXCEPTION);
        return SearchCourseDto.from(c);
    }
}
