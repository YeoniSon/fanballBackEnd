package com.example.fanball.user.entity;

import com.example.common.base.BaseEntity;
import com.example.fanball.user.domain.SignupForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;


@Entity(name = "USER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class User extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String favoriteTeam;

    @Column(unique = true)
    private String email;

    private String name;
    private String nickname;
    private String password;
    private String phone;
    private LocalDate birth;

    private String role;


    private Double reputation = 0.0;
    private Double responseRate = 0.0;

    private Boolean blocked;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verified;

    public static User from(SignupForm form) {
        return User.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))
                .password(form.getPassword())
                .name(form.getName())
                .nickname(form.getNickName())
                .phone(form.getPhone())
                .birth(form.getBirth())
                .role("USER")
                .reputation(0.0)
                .responseRate(0.0)
                .blocked(false)
                .verified(false)
                .build();
    }
}
