package com.example.runway.domain.favorite.service;

import com.example.runway.domain.course.dto.CoursePreviewDto;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.error.CourseFailed;
import com.example.runway.domain.course.repository.CourseRepository;
import com.example.runway.domain.favorite.dto.FavoriteList;
import com.example.runway.domain.favorite.entity.Favorite;
import com.example.runway.domain.favorite.error.FavoriteFailed;
import com.example.runway.domain.favorite.repository.FavoriteRepository;
import com.example.runway.domain.search.dto.SearchCoursesDto;
import com.example.runway.domain.user.entity.User;
import com.example.runway.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public void addFavorite(Long userId, String crsIdx) {
        // 이미 찜한 경우에는 예외 던짐
        if (favoriteRepository.existsByUser_IdAndCourse_CrsIdx(userId, crsIdx)) {
            throw FavoriteFailed.Exception;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
        Course course = courseRepository.findById(crsIdx)
                .orElseThrow(() -> CourseFailed.Exception);

        Favorite fav = Favorite.builder()
                .user(user)
                .course(course)
                .build();

        favoriteRepository.save(fav);
    }

    public void deleteFavorite(Long userId, String crsIdx) {
        favoriteRepository.deleteByUser_IdAndCourse_CrsIdx(userId, crsIdx);
    }

    public List<FavoriteList> getFavoriteCourseList(
            Long userId
    ) {
        List<Course> courses = favoriteRepository.findCourseByUserId(userId);

        Map<String, List<CoursePreviewDto>> grouped = courses.stream()
                .collect(Collectors.groupingBy(
                        c -> {
                            String s = c.getSigun();
                            if (s == null || s.isBlank()) return "??";     // 누락 안전장치
                            return s.substring(0, Math.min(2, s.length()));
                        },
                        LinkedHashMap::new,
                        Collectors.mapping(CoursePreviewDto::from, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(e -> new FavoriteList(e.getKey(), e.getValue()))
                .toList();
    }

}
