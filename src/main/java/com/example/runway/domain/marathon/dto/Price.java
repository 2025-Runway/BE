package com.example.runway.domain.marathon.dto;

import com.example.runway.domain.marathon.entity.MarathonType;
import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Price {
    private String type;
    private String price;

    public static Price from(MarathonType marathonType) {
        return Price.builder()
                .type(marathonType.getName())
                .price(marathonType.getPrice())
                .build();
    }
}
