package com.example.runway.domain.favorite.service;

import com.amazonaws.util.FakeIOException;
import com.example.runway.domain.course.entity.Course;
import com.example.runway.domain.course.error.CourseFailed;
import com.example.runway.domain.course.repository.CourseRepository;
import com.example.runway.domain.favorite.entity.Favorite;
import com.example.runway.domain.favorite.error.FavoriteFailed;
import com.example.runway.domain.favorite.repository.FavoriteRepository;
import com.example.runway.domain.user.User;
import com.example.runway.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
}
