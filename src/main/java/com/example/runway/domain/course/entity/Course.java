package com.example.runway.domain.course.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Id
    @Column(name = "crs_idx", nullable = false, updatable = false)
    private String crsIdx;    // PK

    @Column(name = "route_idx", length = 50, nullable = false)
    private String routeIdx;   // 길 고유번호 (코스가 독립적일 수도 있음)

    @Column(name = "crs_kor_nm", length = 200, nullable = false)
    private String crsKorNm;   // 코스명

    @Column(name = "crs_dstnc", nullable = false)
    private Integer crsDstnc;

    @Column(name = "crs_totl_rqrm_hour", nullable = false)
    private Integer crsTotlRqrmHour;   // 총 소요시간 (분)

    @Column(name = "crs_level", nullable = false)
    private Byte crsLevel;   // 난이도 (1:하, 2:중, 3:상)

    @Column(name = "crs_cycle", length = 50, nullable = false)
    private String crsCycle;   // 순환형태 (순환형/비순환형)

    @Lob
    @Column(name = "crs_contents", columnDefinition = "LONGTEXT")
    private String crsContents;   // 코스 설명 (HTML 포함)

    @Lob
    @Column(name = "crs_summary", columnDefinition = "LONGTEXT")
    private String crsSummary;   // 코스 개요

    @Lob
    @Column(name = "crs_tour_info", columnDefinition = "LONGTEXT")
    private String crsTourInfo;   // 관광 포인트

    @Lob
    @Column(name = "travelerinfo", columnDefinition = "LONGTEXT")
    private String travelerinfo;   // 여행자 정보

    @Column(name = "sigun", length = 100)
    private String sigun;   // 행정구역 (예: 경남 밀양시)

    @Enumerated(EnumType.STRING)
    @Column(name = "brd_div", nullable = false, length = 10)
    private BrdDiv brdDiv;   // 길 구분 (DNVW: 걷기길, DNBW: 자전거길)

    @Column(name = "gpx_file_path", length = 500)
    private String gpxFilePath;   // GPX 파일 경로 (URL)

    @Column(name = "crs_img", length = 200)
    private String crsImg;   // 코스 사진

    // 길 구분 Enum
    public enum BrdDiv {
        DNWW, // 걷기길
        DNBW  // 자전거길
    }
}
