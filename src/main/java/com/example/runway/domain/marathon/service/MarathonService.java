package com.example.runway.domain.marathon.service;

import com.example.runway.domain.marathon.dto.MarathonListDto;
import com.example.runway.domain.marathon.repository.MarathonRepository;
import com.example.runway.domain.marathon.repository.MarathonTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarathonService {
    private final MarathonRepository marathonRepository;
    private final MarathonTypeRepository marathonTypeInterface;
    private final MarathonTypeRepository marathonTypeRepository;

    public List<MarathonListDto> marathonList() {
        List<MarathonListDto> marathons = (List<MarathonListDto>) marathonRepository.findAll().stream().map(
                marathon -> MarathonListDto.toDto(marathon, marathonTypeRepository.findNamesByMarathon(marathon))
        ).toList();

        return marathons;
    }
}
