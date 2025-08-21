package com.example.runway.domain.course.dto;

import com.example.runway.domain.course.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseDto(
        String crsIdx,
        String routeIdx,
        String crsKorNm,
        Integer crsDstnc,
        Integer crsTotlRqrmHour,
        Byte crsLevel,
        String crsCycle,

        String crsContents,
        String crsSummary,
        String crsTourInfo,
        String travelerinfo,

        String sigun,
        Course.BrdDiv brdDiv,
        String gpxFilePath,
        String crsImg,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CourseDto from(Course c) {
        return new CourseDto(
                c.getCrsIdx(),
                c.getRouteIdx(),
                c.getCrsKorNm(),
                c.getCrsDstnc(),
                c.getCrsTotlRqrmHour(),
                c.getCrsLevel(),
                c.getCrsCycle(),

                c.getCrsContents(),
                c.getCrsSummary(),
                c.getCrsTourInfo(),
                c.getTravelerinfo(),

                c.getSigun(),
                c.getBrdDiv(),
                c.getGpxFilePath(),
                c.getCrsImg(),

                c.getCreated_at(),
                c.getUpdated_at()
        );
    }
}
