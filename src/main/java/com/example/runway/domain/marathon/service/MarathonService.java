package com.example.runway.domain.marathon.service;

import com.example.runway.domain.marathon.dto.CourseInfo;
import com.example.runway.domain.marathon.dto.MarathonDto;
import com.example.runway.domain.marathon.dto.MarathonListDto;
import com.example.runway.domain.marathon.dto.Price;
import com.example.runway.domain.marathon.entity.Marathon;
import com.example.runway.domain.marathon.repository.MarathonCourseRepository;
import com.example.runway.domain.marathon.repository.MarathonRepository;
import com.example.runway.domain.marathon.repository.MarathonTypeRepository;
import com.example.runway.domain.search.error.NotFoundCourse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonRepository marathonRepository;
    private final MarathonTypeRepository marathonTypeRepository;
    private final MarathonCourseRepository marathonCourseRepository;

    public Page<MarathonListDto> marathonList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Marathon> marathons = marathonRepository.findAll(pageable);
        Page<MarathonListDto> dtoPage = marathons.map(marathon -> {
            List<String> types = marathonTypeRepository.findNamesByMarathon(marathon);
            return MarathonListDto.from(marathon, types);
        });
        return dtoPage;

    }

    public MarathonDto getMarathon(Long marathonId) {
        Marathon marathon = marathonRepository.findById(marathonId)
                .orElseThrow(() -> NotFoundCourse.EXCEPTION);
        List<Price> prices = marathonTypeRepository.findByMarathon(marathon)
                .stream()
                .map(Price::from)
                .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getPrice().replaceAll("[^0-9]", ""))))
                .toList();

        List<CourseInfo> infos = marathonCourseRepository.findCourseByMarathonId(marathonId, PageRequest.of(0, 5))
                .stream().map(CourseInfo::from)
                .toList();
        return MarathonDto.toDto(marathon, infos, prices);
    }
}
