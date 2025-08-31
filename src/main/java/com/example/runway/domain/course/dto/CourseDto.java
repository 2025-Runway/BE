package com.example.runway.domain.course.dto;

import com.example.runway.domain.course.entity.BrdDiv;
import com.example.runway.domain.course.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseDto(
        String crsIdx,
        String routeIdx,
        String crsKorNm,
        Integer crsDstnc,
        Integer crsTotlRqrmHour,
        Integer crsLevel,
        String crsCycle,

        String crsContents,
        String crsSummary,
        String crsTourInfo,
        String travelerinfo,

        String sigun,
        BrdDiv brdDiv,
        String gpxFilePath,
        String crsImg,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CourseDto from(Course c) {
        return new CourseDto(
                c.getCrsIdx(),
                c.getRoute().getRouteIdx(),
                c.getCrsKorNm(),
                c.getCrsDstnc(),
                c.getCrsTotlRqrmHour(),
                c.getCrsLevel(),
                c.getCrsCycle(),

                c.getCrsContents(),
                c.getCrsSummary(),
                c.getCrsTourInfo(),
                c.getTravelerInfo(),

                c.getSigun(),
                c.getBrdDiv(),
                c.getGpxPath(),
                c.getCrsImgUrl(),

                c.getCreatedTime(),
                c.getModifiedTime()
        );
    }
}
