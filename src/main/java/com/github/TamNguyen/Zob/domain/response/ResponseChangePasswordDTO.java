package com.github.TamNguyen.Zob.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseChangePasswordDTO {
    private long id;
    private String email;
    private String name;
    private Instant updatedAt;
}
