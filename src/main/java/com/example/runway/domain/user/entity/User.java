package com.example.runway.domain.user.entity;

import com.example.runway.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "`user`",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_kakao_id",
                        columnNames = {"kakao_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 PK

    @Column(name = "kakao_id", nullable = false, length = 100)
    private String kakaoId; // 카카오 고유 ID

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname; // 카카오 닉네임

    @Column(name = "email", length = 255)
    private String email; // 카카오 계정 이메일 (없을 수 있음)

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl; // 카카오 프로필 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;



    public void updateProfile(String nickname, String email, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (email != null) this.email = email;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }
}
