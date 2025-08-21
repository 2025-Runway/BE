package com.example.runway.domain.course.service;

import com.example.runway.domain.course.dto.CourseDto;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.error.CourseFailed;
import com.example.runway.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseDto getById(String crsIdx) {
        Course c = courseRepository.findById(crsIdx)
                .orElseThrow(() -> CourseFailed.Exception);
        return CourseDto.from(c);
    }
}
