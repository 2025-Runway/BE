package com.example.runway.domain.tourInfo.service;

import com.example.runway.domain.tourInfo.dto.TourInfoDto;
import com.example.runway.domain.tourInfo.entity.CourseTour;
import com.example.runway.domain.tourInfo.entity.TourInfo;
import com.example.runway.domain.tourInfo.repository.CourseTourRepository;
import com.example.runway.domain.tourInfo.util.ContentTypeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourInfoService {
    private final CourseTourRepository courseTourRepository;

    public Page<TourInfoDto> getByCrsIdx(String crsIdx, String contentType, int page) {
        String contentTypeId = ContentTypeUtil.toContentTypeId(contentType);
        Pageable pageable = PageRequest.of(page, 10);
        Page<TourInfo> tourInfoList = courseTourRepository.findTourInfosByCrsIdxAndOptionalContentTypeId(crsIdx, contentTypeId, pageable);
        return tourInfoList.map(TourInfoDto::from);
    }
}
