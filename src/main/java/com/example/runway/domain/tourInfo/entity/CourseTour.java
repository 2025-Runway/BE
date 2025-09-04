package com.example.runway.domain.tourInfo.entity;
import com.example.runway.domain.course.entity.Course;
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
@Table(name = "course_tour")
public class CourseTour extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_tour_id")
    private Long courseTourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crs_idx", nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_tour_course"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_info_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_course_tour_tour_info"))
    private TourInfo tourInfo;
}
