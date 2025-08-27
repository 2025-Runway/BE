package com.example.runway.domain.favorite.entity;

import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.user.entity.User;
import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
        name = "user_course_favorite",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_course",
                columnNames = {"user_id", "crs_idx"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 단일 PK

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_user"))
    private User user;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "crs_idx", nullable = false,
            foreignKey = @ForeignKey(name = "fk_fav_course"))
    private Course course;
}
