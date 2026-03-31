package com.github.TamNguyen.Zob.domain.response;

import java.time.Instant;

import com.github.TamNguyen.Zob.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
