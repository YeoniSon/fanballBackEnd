package com.example.fanball.user.dto.user;

import com.example.fanball.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String favoriteTeam;
    private String phone;
    private LocalDate birth;
    private String role;

    private Double reputation;
    private Double responseRate;
    private Boolean blocked;

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getFavoriteTeam(),
                user.getPhone(),
                user.getBirth(),
                user.getRole(),
                user.getReputation(),
                user.getResponseRate(),
                user.getBlocked());
    }
}
