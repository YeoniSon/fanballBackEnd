package com.example.fanball.user.dto.user.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequestDto {

    @JsonAlias({"nickName"})
    private String nickname;
    private String favoriteTeam;

    @Getter
    @Setter
    public static class ReputationRequest {
        private Double reportScore;
    }

    @Getter
    @Setter
    public static class ResponseRateRequest {
        private Long respondedCount;
        private Long requestCount;
    }
}
