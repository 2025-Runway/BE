package com.example.runway.domain.tourInfo.dto;

import com.example.runway.domain.tourInfo.entity.TourInfo;
import com.example.runway.domain.tourInfo.util.ContentTypeUtil;

public record TourInfoDto(
        String contentId,
        String contentType,
        String title,
        String category,
        String address,
        String imageUrl,
        double mapX,
        double mapY
) {
    public static TourInfoDto from(TourInfo tourInfo) {
        return new TourInfoDto(
                tourInfo.getContentId(),
                ContentTypeUtil.toLabel(tourInfo.getContentTypeId()),
                tourInfo.getTitle(),
                tourInfo.getDetailCategory(),
                tourInfo.getAddr1(),
                tourInfo.getFirstImage(),
                tourInfo.getMapX(),
                tourInfo.getMapY()
        );
    }

}
