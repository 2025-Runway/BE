package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.marathon.entity.Marathon;

import java.util.List;

public record MarathonDto(
        Long marathonId,
        String title,
        String month,
        String day,
        String dayOfWeek,
        String addr,
        String host,
        List<Price> prices,
        //List<CourseInfo> courseInfos,
        String homepageUrl

) {
    public static MarathonDto toDto(Marathon marathon, List<Price> prices) {
        return new MarathonDto(
                marathon.getId(),
                marathon.getTitle(),
                String.format("%02d", marathon.getMonth()),
                String.format("%02d", marathon.getDay()),
                marathon.getDayOfWeek(),
                marathon.getAddr1() + ", " + marathon.getAddr2(),
                marathon.getHost(),
                prices,
                //courseInfos,
                marathon.getHomepageUrl()
        );
    }
}
