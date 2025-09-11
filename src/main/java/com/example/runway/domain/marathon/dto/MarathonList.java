package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.marathon.entity.Marathon;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MarathonList {
    private Long marathonId;
    private String title;
    private String addr;
    private String month;
    private String day;
    private String dayOfWeek;
    private boolean isApplying;
    private List<String> types;

    public static MarathonList of(Marathon marathon, List<String> types) {
        return MarathonList.builder()
                .marathonId(marathon.getId())
                .title(marathon.getTitle())
                .addr(marathon.getAddr1() + ", " + marathon.getAddr2())
                .month(String.format("%02d", marathon.getMonth()))
                .day(String.format("%02d", marathon.getDay()))
                .dayOfWeek(marathon.getDayOfWeek())
                .isApplying(marathon.isApplying())
                .types(types)
                .build();
    }
}
