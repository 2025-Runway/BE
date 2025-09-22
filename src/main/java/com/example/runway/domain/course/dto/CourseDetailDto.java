package com.example.runway.domain.course.dto;

import com.example.runway.domain.course.entity.BrdDiv;
import com.example.runway.domain.course.entity.Course;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CourseDetailDto(
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
        LocalDateTime updatedAt,
        boolean isFavorite
) {
    public static CourseDetailDto of(Course c, boolean isFavorite) {
        return CourseDetailDto.builder()
                .crsIdx(c.getCrsIdx())
                .routeIdx(c.getRoute().getRouteIdx())
                .crsKorNm(c.getCrsKorNm())
                .crsDstnc(c.getCrsDstnc())
                .crsTotlRqrmHour(c.getCrsTotlRqrmHour())
                .crsLevel(c.getCrsLevel())
                .crsCycle(c.getCrsCycle())
                .crsContents(c.getCrsContents())
                .crsSummary(c.getCrsSummary())
                .crsTourInfo(c.getCrsTourInfo())
                .travelerinfo(c.getTravelerInfo())
                .sigun(c.getSigun())
                .brdDiv(c.getBrdDiv())
                .gpxFilePath(c.getGpxPath())
                .crsImg(c.getCrsImgUrl())
                .createdAt(c.getCreatedTime())
                .updatedAt(c.getModifiedTime())
                .isFavorite(isFavorite)
                .build();
    }
}