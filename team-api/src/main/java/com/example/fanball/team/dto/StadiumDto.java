package com.example.fanball.team.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StadiumDto {
    private String id;
    private String name;
    private String location;
    private List<SectionDto> sections; // 2군은 null
}
