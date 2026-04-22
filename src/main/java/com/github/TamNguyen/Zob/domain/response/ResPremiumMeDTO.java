package com.github.TamNguyen.Zob.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResPremiumMeDTO {
    private boolean isPremium;
    private Instant startAt;
    private Instant endAt;
}
