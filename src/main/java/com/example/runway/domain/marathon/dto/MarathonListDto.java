package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.marathon.entity.Marathon;
import com.example.runway.global.entity.PageInfo;

import java.util.List;

public record MarathonListDto<T>(
        T data,
        PageInfo pageInfo

) {
    public static MarathonListDto from(Marathon marathon, List<String> types) {
        MarathonList marathonList = MarathonList.of(marathon, types);
        return new MarathonListDto(
                marathonList,
                null
        );
    }

    public MarathonListDto<T> toDto(T data, PageInfo pageInfo) {
        return new MarathonListDto(
                data,
                pageInfo
        );

    }
}
