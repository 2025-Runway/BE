package com.example.runway.domain.user.entity;

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
@Table(name = "region_images")
public class RegionImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_image_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;
}
