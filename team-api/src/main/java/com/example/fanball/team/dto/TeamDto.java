package com.example.fanball.team.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDto {
    private String team_name;
    private String short_name;
    private String logo;
    private Integer level;
    private String league;
    private StadiumDto stadium;
}
