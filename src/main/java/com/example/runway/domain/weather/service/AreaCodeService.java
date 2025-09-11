package com.example.runway.domain.weather.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AreaCodeService {

    private final Map<String, String> areaCodeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource("data/area_code.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "EUC-KR"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2 && "존재".equals(parts[parts.length - 1])) {
                    String code = parts[0];
                    String address = parts[1];
                    String[] addressParts = address.split(" ");
                    if (addressParts.length >= 2) {
                        areaCodeMap.put(addressParts[0] + " " + addressParts[1], code);
                    }
                }
            }
            log.info("성공적으로 {}개의 지역 코드를 로드했습니다.", areaCodeMap.size());
        } catch (Exception e) {
            throw new RuntimeException("필수적인 지역 코드 파일을 로드할 수 없습니다.", e);
        }
    }

    public Optional<String> getAreaCode(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }

        // 주소를 공백으로 분리
        String[] addressParts = address.split(" ");

        // 주소가 "시/도 시/군/구" 이상으로 길면, 앞 두 부분만 잘라 KEY로 사용
        if (addressParts.length >= 2) {
            String simplifiedKey = addressParts[0] + " " + addressParts[1];
            return Optional.ofNullable(areaCodeMap.get(simplifiedKey));
        }

        // "서울특별시" 처럼 주소가 한 부분일 경우 그대로 사용
        return Optional.ofNullable(areaCodeMap.get(address));
    }
}