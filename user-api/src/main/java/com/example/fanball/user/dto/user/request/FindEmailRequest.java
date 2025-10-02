package com.example.fanball.user.dto.user.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindEmailRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
}


