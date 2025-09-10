package com.example.runway.domain.course.service;

import com.example.runway.domain.course.entity.Course;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;


    public String generateCourseAnalysis(Course course) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        String prompt = buildPromptFromCourse(course);

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(prompt.replace("\"", "\\\""));

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String rawResponse = restTemplate.postForObject(apiUrl, requestEntity, String.class);
        log.info("Gemini Raw Response: {}", rawResponse);

        return extractTextAndCleanJson(rawResponse);
    }

    /**
     * Gemini 응답 JSON 구조를 파싱하여 'text' 필드의 값을 추출하고,
     * 그 값에서 한번 더 markdown 코드 블록을 제거합니다.
     * @param rawResponse Gemini API의 원본 응답 문자열
     * @return 순수한 JSON 형식의 문자열 (예: {"analysis":[...]})
     * @throws IOException 파싱 실패 시
     */
    private String extractTextAndCleanJson(String rawResponse) throws IOException {
        if (rawResponse == null) return null;

        // 1. 전체 응답을 JSON 객체로 파싱
        JsonNode rootNode = objectMapper.readTree(rawResponse);

        // 2. 경로를 따라 'text' 필드까지 접근
        String textContent = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        if (textContent.isEmpty()) {
            return null;
        }

        // 3. 'text' 필드 값에서 markdown 코드 블록 제거
        int startIndex = textContent.indexOf('{');
        int endIndex = textContent.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return textContent.substring(startIndex, endIndex + 1);
        }

        return textContent; // 코드 블록이 없는 순수 JSON일 경우
    }
    /**
     * Course 엔티티 정보를 바탕으로 Gemini에게 보낼 프롬프트를 생성합니다.
     * @param course Course 객체
     * @return 완성된 프롬프트 문자열
     */
    private String buildPromptFromCourse(Course course) {
        return String.format("""
                너는 코스 설명 데이터를 분석하여 List<String> 형태의 JSON 배열을 생성하는 API야.
                아래 '코스 설명 데이터'를 분석해서, 다음 JSON 형식과 정확히 일치하도록 결과만 응답해.
                JSON 앞뒤에 다른 설명이나 markdown 문법을 절대 붙이지 마.

                ## JSON 응답 형식:
                {
                  "analysis": [
                    "코스에 대한 전반적인 요약 및 난이도 평가 문장 1개",
                    "초보자를 위한 팁 문장 2개",
                    "중급자 이상을 위한 팁 문장 2개"
                  ]
                }

                ## 코스 설명 데이터:
                * 코스 이름: %s
                * 코스 거리: %d km
                * 코스 요약: %s
                * 코스 상세 설명: %s
                """,
                course.getCrsKorNm(),
                course.getCrsDstnc(),
                course.getCrsSummary(),
                course.getCrsContents()
        );
    }
}