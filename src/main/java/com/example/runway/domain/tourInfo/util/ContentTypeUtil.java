package com.example.runway.domain.tourInfo.util;

import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import com.example.runway.domain.tourInfo.entity.TourInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentTypeUtil {
    public static String toLabel(String contentTypeId) {
        return switch (contentTypeId) {
            case "12" -> "관광지";
            case "14" -> "문화시설";
            case "15" -> "행사/공연/축제";
            case "28" -> "레포츠";
            case "32" -> "숙박";
            case "38" -> "쇼핑";
            case "39" -> "음식점";
            default -> "기타";
        };
    }

    public static String toContentTypeId(String label) {
        return switch (label != null ? label : "NULL") {
            case "관광지" -> "12";
            case "문화시설" -> "14";
            case "행사/공연/축제" -> "15";
            case "레포츠" -> "28";
            case "숙박" -> "32";
            case "쇼핑" -> "38";
            case "음식점" -> "39";
            default -> null;
        };
    }
}
