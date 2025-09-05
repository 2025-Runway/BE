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
public class KmaMidTermRegionService {

    private final Map<String, String> landRegionCodeMap = new HashMap<>(); // 육상예보용
    private final Map<String, String> tempRegionCodeMap = new HashMap<>(); // 기온예보용
    private final Map<String, String> provinceToRepresentativeCity = new HashMap<>(); // 각 도의 대표 도시를 매핑하는 Map 추가


    @PostConstruct
    public void init() {
        loadData("data/mid_term_land_codes.txt", landRegionCodeMap);
        loadData("data/mid_term_temp_codes" +
                ".txt", tempRegionCodeMap);

        // 대표 도시 정보 초기화
        provinceToRepresentativeCity.put("경기도", "수원");
        provinceToRepresentativeCity.put("강원도", "춘천"); // 영서/영동 나뉠 수 있으나 춘천으로 통일
        provinceToRepresentativeCity.put("충청북도", "청주");
        provinceToRepresentativeCity.put("충청남도", "대전"); // 서산도 있지만 대전으로 통일
        provinceToRepresentativeCity.put("전라북도", "전주");
        provinceToRepresentativeCity.put("전라남도", "광주"); // 목포/여수도 있지만 광주로 통일
        provinceToRepresentativeCity.put("경상북도", "대구"); // 안동/포항도 있지만 대구로 통일
        provinceToRepresentativeCity.put("경상남도", "부산"); // 창원/울산도 있지만 부산으로 통일
        provinceToRepresentativeCity.put("제주특별자치도", "제주");
    }

    private void loadData(String path, Map<String, String> map) {
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
            log.info("성공적으로 {}에서 {}개의 지역 코드를 로드했습니다.", path, map.size());
        } catch (Exception e) {
            log.error("{} 파일 로드에 실패했습니다.", path, e);
            throw new RuntimeException(path + " 파일 로드 실패", e);
        }
    }

    // 육상 예보(날씨) 지역 코드 조회
    public Optional<String> getLandRegionId(String address) {
        if (address == null || address.isEmpty()) return Optional.empty();

        String province = address.split(" ")[0];
        if (province.equals("강원특별자치도")) province = "강원도";
        if (province.equals("전북특별자치도")) province = "전라북도";

        return Optional.ofNullable(landRegionCodeMap.get(province));
    }

    // 기온 예보 지역 코드 조회
    public Optional<String> getTempRegionId(String address) {
        if (address == null || address.isEmpty()) return Optional.empty();

        String[] parts = address.split(" ");
        String province = parts[0];
        String city = (parts.length > 1) ? parts[1] : province;
        String keyToLookup;

        // 광역시/특별시는 대표 이름으로 키를 설정
        if (province.startsWith("서울")) keyToLookup = "서울";
        else if (province.startsWith("인천")) keyToLookup = "인천";
        else if (province.startsWith("부산")) keyToLookup = "부산";
        else if (province.startsWith("대구")) keyToLookup = "대구";
        else if (province.startsWith("광주")) keyToLookup = "광주";
        else if (province.startsWith("대전")) keyToLookup = "대전";
        else if (province.startsWith("울산")) keyToLookup = "울산";
        else if (province.startsWith("세종")) keyToLookup = "세종";
        else {
            // 2. 그 외 지역은 '시/군' 이름으로 키를 설정
            keyToLookup = city.endsWith("시") || city.endsWith("군") ? city.substring(0, city.length() - 1) : city;
        }

        // 3. 설정된 키로 먼저 조회
        String regionId = tempRegionCodeMap.get(keyToLookup);

        // 4. 조회 실패 시, 해당 도의 대표 도시로 대체 조회 (Fallback)
        if (regionId == null) {
            String representativeCity = provinceToRepresentativeCity.get(province);
            if (representativeCity != null) {
                regionId = tempRegionCodeMap.get(representativeCity);
            }
        }

        return Optional.ofNullable(regionId);
    }
}