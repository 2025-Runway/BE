package com.example.runway.domain.marathon.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "marathons")
public class Marathon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marathon_id")
    private Long id;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "day", nullable = false)
    private int day;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "addr1", nullable = false)
    private String addr1;

    @Column(name = "addr2", nullable = false)
    private String addr2;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "prize", nullable = false)
    private String prize;

    @Column(name = "homepage_url", nullable = false)
    private String homepageUrl;

    @Column(name = "is_applying", nullable = false)
    private boolean isApplying;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;


}
