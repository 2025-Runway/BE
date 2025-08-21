package com.example.runway.domain.course.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "routes")
public class Route extends BaseEntity {
    @Id
    @Column(name = "route_idx", nullable = false, unique = true)
    private String routeIdx;

    @Column(name = "theme_nm", nullable = false)
    private String themeNm;

    @Column(name = "line_msg", nullable = false)
    private String lineMsg;

    @Column(name = "theme_descs", nullable = false)
    private String themeDescs;

    @Enumerated(EnumType.STRING)
    @Column(name = "brd_div", nullable = false)
    private BrdDiv brdDiv;

    @Column(name = "created_time", nullable = true)
    private LocalDateTime createdTime;

    @Column(name = "modified_time", nullable = true)
    private LocalDateTime modifiedTime;
}
