package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.course.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseInfo {
    private String crsIdx;
    private String crsKorNm;
    private String sigun;
    private String crsImgUrl;

    public static CourseInfo from(Course course) {
        return new CourseInfo(
                course.getCrsIdx(),
                course.getCrsKorNm(),
                course.getSigun(),
                course.getCrsImgUrl()
        );
    }
}
