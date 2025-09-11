package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.marathon.entity.Marathon;

import java.util.List;

public record MarathonListDto(
        Long id,
        String title,
        int month,
        int day,
        String dayOfWeek,
        String addr,
        List<String> types

) {
    public static MarathonListDto toDto(Marathon marathon, List<String> types) {
        String address = marathon.getAddr1() +" "+ marathon.getAddr2();
        return new MarathonListDto(
                marathon.getId(),
                marathon.getTitle(),
                marathon.getMonth(),
                marathon.getDay(),
                marathon.getDayOfWeek(),
                address,
                types
        );

    }
}
