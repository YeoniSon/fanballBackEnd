package com.example.fanball.team.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SectionDto {
    private String name;
    private Object price; // weekday, weekend, g1~g4등 다양한 key 가능
}
