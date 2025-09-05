package com.example.runway.domain.search.service;

import com.example.runway.domain.search.domain.Keyword;
import com.example.runway.domain.search.dto.KeywordDto;
import com.example.runway.domain.search.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public List<KeywordDto> getKeywords(Long userId) {
        List<Keyword> keywords = keywordRepository.findByUserId(userId);
        return keywords.stream()
                .map(KeywordDto::from)
                .toList();
    }
}
