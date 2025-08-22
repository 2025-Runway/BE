package com.example.runway.domain.course.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course extends BaseEntity {
    @Id
    @Column(name = "crs_idx", nullable = false, unique = true)
    private String crsIdx;

    @Column(name = "crs_kor_nm", nullable = false)
    private String crsKorNm;

    @Column(name = "crs_dstnc", nullable = false)
    private int crsDstnc;

    @Column(name = "crs_totl_rqrm_hour", nullable = false)
    private int crsTotlRrrmHour;

    @Column(name = "crs_level", nullable = false, columnDefinition = "TINYINT")
    private int crsLevel;

    @Column(name = "crs_cycle", nullable = false)
    private String crsCycle;

    @Column(name = "crs_contents", nullable = true, columnDefinition = "TEXT")
    private String crsContents;

    @Column(name = "crs_summary", nullable = true, columnDefinition = "TEXT")
    private String crsSummary;

    @Column(name = "crs_tour_info", nullable = true, columnDefinition = "TEXT")
    private String crsTourInfo;

    @Column(name = "traveler_info", nullable = true, columnDefinition = "TEXT")
    private String travelerInfo;

    @Column(name = "sigun", nullable = true)
    private String sigun;

    @Enumerated(EnumType.STRING)
    @Column(name = "brd_div", nullable = false)
    private BrdDiv brdDiv;

    @Column(name = "gpxpath", nullable = true)
    private String gpxPath;

    @Column(name = "crs_img_url", nullable = true)
    private String crsImgUrl;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "modified_time", nullable = true)
    private LocalDateTime modifiedTime;
}
