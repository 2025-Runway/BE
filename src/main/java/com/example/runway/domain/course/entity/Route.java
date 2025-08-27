package com.example.runway.domain.course.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "routes")
public class Route {
    @Id
    @Column(name = "route_idx", nullable = false, unique = true)
    private String routeIdx;

    @Column(name = "theme_nm", nullable = false)
    private String themeNm;

    @Column(name = "line_msg", nullable = false, columnDefinition = "TEXT")
    private String lineMsg;

    @Column(name = "theme_descs", nullable = false, columnDefinition = "TEXT")
    private String themeDescs;

    @Enumerated(EnumType.STRING)
    @Column(name = "brd_div", nullable = false)
    private BrdDiv brdDiv;

    @Column(name = "created_time", nullable = true)
    private LocalDateTime createdTime;

    @Column(name = "modified_time", nullable = true)
    private LocalDateTime modifiedTime;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL) // Course 엔티티의 'route' 필드에 매핑
    private List<Course> courses;
}
