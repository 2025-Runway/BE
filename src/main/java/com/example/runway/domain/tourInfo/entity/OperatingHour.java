package com.example.runway.domain.tourInfo.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "operating_hours")
public class OperatingHour extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operating_hour_id")
    public Long operatingHourId;

    @Column(name = "mon_open", nullable = true)
    public LocalTime monOpen;

    @Column(name = "mon_close", nullable = true)
    public LocalTime monClose;

    @Column(name = "tue_open", nullable = true)
    public LocalTime tueOpen;

    @Column(name = "tue_close", nullable = true)
    public LocalTime tueClose;

    @Column(name = "wed_open", nullable = true)
    public LocalTime wedOpen;

    @Column(name = "wed_close", nullable = true)
    public LocalTime wedClose;

    @Column(name = "thur_open", nullable = true)
    public LocalTime thurOpen;

    @Column(name = "thur_close", nullable = true)
    public LocalTime thurClose;

    @Column(name = "fri_open", nullable = true)
    public LocalTime friOpen;

    @Column(name = "fri_close", nullable = true)
    public LocalTime friClose;

    @Column(name = "sat_open", nullable = true)
    public LocalTime satOpen;

    @Column(name = "sat_close", nullable = true)
    public LocalTime satClose;

    @Column(name = "sun_open", nullable = true)
    public LocalTime sunOpen;

    @Column(name = "sun_close", nullable = true)
    public LocalTime sunClose;
}
