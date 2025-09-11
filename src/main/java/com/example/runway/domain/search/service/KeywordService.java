package com.example.runway.domain.search.service;

import com.example.runway.domain.search.domain.Keyword;
import com.example.runway.domain.search.dto.KeywordDto;
import com.example.runway.domain.search.repository.KeywordRepository;
import com.example.runway.domain.search.validator.SearchKeywordValidator;
import com.example.runway.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final UserService userService;
    private final SearchKeywordValidator searchKeywordValidator;

    public List<KeywordDto> getKeywords(Long userId) {
        List<Keyword> keywords = keywordRepository.findByUserId(userId);
        return keywords.stream()
                .map(KeywordDto::from)
                .toList();
    }

    public void deleteKeyword(Long userId, Long keywordId) {
        keywordRepository.deleteByUserIdAndKeywordId(userId, keywordId);
    }

    public void addKeyword(Long userId, String keyword) {
        // 유저 존재 여부 검증
        if(searchKeywordValidator.userValidate(userId)){
            if(keywordRepository.existsByUserIdAndWord(userId, keyword)) {
                keywordRepository.deleteByUserIdAndWord(userId, keyword);
            }

            Keyword newKeyword = Keyword.builder()
                    .user(userService.getUser(userId))
                    .word(keyword)
                    .build();
            keywordRepository.save(newKeyword);
        };


    }
}
