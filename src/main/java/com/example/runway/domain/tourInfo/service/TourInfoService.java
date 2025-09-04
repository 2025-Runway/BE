package com.example.runway.domain.tourInfo.service;

import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import com.example.runway.domain.tourInfo.entity.CourseTour;
import com.example.runway.domain.tourInfo.entity.TourInfo;
import com.example.runway.domain.tourInfo.repository.CourseTourRepository;
import com.example.runway.domain.tourInfo.util.ContentTypeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourInfoService {
    private final CourseTourRepository courseTourRepository;

    public List<TourInfoDto> getByCrsIdx(String crsIdx, String contentType) {
        String contentTypeId = ContentTypeUtil.toContentTypeId(contentType);
        List<TourInfo> tourInfoList = courseTourRepository.findTourInfosByCrsIdxAndOptionalContentTypeId(crsIdx, contentTypeId);
        return tourInfoList.stream().map(TourInfoDto::from).toList();
    }
}
