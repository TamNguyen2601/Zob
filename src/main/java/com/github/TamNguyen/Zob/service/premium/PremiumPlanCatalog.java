package com.github.TamNguyen.Zob.service.premium;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;
import com.github.TamNguyen.Zob.util.error.ValidationErrorException;

@Service
public class PremiumPlanCatalog {

    public record Plan(long amountVnd) {
    }

    private final Map<PremiumPlanCode, Plan> plans = new EnumMap<>(PremiumPlanCode.class);

    public PremiumPlanCatalog() {
        plans.put(PremiumPlanCode.DEMO_1_MIN, new Plan(3_000L));
        plans.put(PremiumPlanCode.MONTH_1, new Plan(50_000L));
        plans.put(PremiumPlanCode.MONTH_3, new Plan(100_000L));
        plans.put(PremiumPlanCode.YEAR_1, new Plan(250_000L));
    }

    public long getAmountVnd(PremiumPlanCode code) {
        Plan plan = plans.get(code);
        if (plan == null) {
            throw new ValidationErrorException("Premium plan không hợp lệ");
        }
        return plan.amountVnd();
    }

    public Instant addDuration(Instant base, PremiumPlanCode code) {
        if (code == null) {
            throw new ValidationErrorException("Premium plan không hợp lệ");
        }

        return switch (code) {
            case DEMO_1_MIN -> base.plusSeconds(60);
            case MONTH_1 -> addMonths(base, 1);
            case MONTH_3 -> addMonths(base, 3);
            case YEAR_1 -> addYears(base, 1);
        };
    }

    private Instant addMonths(Instant base, int months) {
        ZonedDateTime zdt = base.atZone(ZoneOffset.UTC);
        return zdt.plusMonths(months).toInstant();
    }

    private Instant addYears(Instant base, int years) {
        ZonedDateTime zdt = base.atZone(ZoneOffset.UTC);
        return zdt.plusYears(years).toInstant();
    }
}
