package com.example.runway.domain.tourInfo.entity;

import com.example.runway.domain.course.entity.Route;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tour_info")
public class TourInfo {
    @Id
    @Column(name = "contentid", nullable = false, unique = true)
    private String contentId;

    @Column(name = "contenttypeid", nullable = false)
    private String contentTypeId;

    @Column(name = "addr1", nullable = false)
    private String addr1;

    @Column(name = "addr2", nullable = true)
    private String addr2;

    @Column(name = "zipcode", nullable = false)
    private String zipCode; // 우편번호

    @Column(name = "areacode", nullable = false)
    private int areaCode; // 시도코드

    @Column(name = "cat1", nullable = true)
    private String cat1; // 대분류 코드

    @Column(name = "cat2", nullable = true)
    private String cat2; // 중분류 코드

    @Column(name = "cat3", nullable = true)
    private String cat3; // 소분류 코드

    @Column(name = "createdtime", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "modifiedtime", nullable = false)
    private LocalDateTime modifiedTime;

    @Column(name = "firstimage", nullable = true)
    private String firstImage; // 대표이미지

    @Column(name = "firstimage2", nullable = true)
    private String firstImage2; // 대표이미지2

    @Column(name = "cpyrht_div_cd", nullable = true)
    private String cpyrhtDivCd; // 저작권 구분 코드

    @Column(name = "mapx", nullable = false, columnDefinition = "Decimal(13,10)")
    private double mapX; // 경도

    @Column(name = "mapy", nullable = false, columnDefinition = "Decimal(12,10)")
    private double mapY; // 위도

    @Column(name = "mlevel", nullable = false)
    private int mLevel; // 지도 레벨

    @Column(name = "sigungucode", nullable = true)
    private int sigunguCode; // 시군구코드

    @Column(name = "tel", nullable = true)
    private String tel; // 전화번호

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @Column(name = "lDong_regn_cd", nullable = false)
    private String lDongRegnCd; // 법정동 코드

    @Column(name = "lDong_signgu_cd", nullable = false)
    private String lDongSignguCd; // 법정동 시군구 코드

    @Column(name = "lcls_systm1", nullable = false)
    private String lclsSystm1; // 대분류 체계

    @Column(name = "lcls_systm2", nullable = false)
    private String lclsSystm2; // 중분류 체계

    @Column(name = "lcls_systm3", nullable = false)
    private String lclsSystm3; // 소분류 체계

    @Column(name = "detail_category", nullable = true)
    private String detailCategory; // 상세 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operating_hour_id") // OperatingHour 엔티티의 기본키(operating_hour_id)를 외래키로 연결
    private OperatingHour oepratingHour;

}
