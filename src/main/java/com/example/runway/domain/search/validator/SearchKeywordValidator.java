package com.example.runway.domain.search.validator;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.search.error.EmptyKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchKeywordValidator {
    public void keywordExistValidate(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw EmptyKeyword.EXCEPTION;
        }
    }

    public void resultValidate(List<Course> courses) {
        if (courses.isEmpty()) {
            throw EmptyKeyword.EXCEPTION;
        }
    }
}
