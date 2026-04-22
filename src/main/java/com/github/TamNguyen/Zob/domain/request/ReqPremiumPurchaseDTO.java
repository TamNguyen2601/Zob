package com.github.TamNguyen.Zob.domain.request;

import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

import jakarta.validation.constraints.NotNull;

public class ReqPremiumPurchaseDTO {

    @NotNull(message = "planCode is required")
    private PremiumPlanCode planCode;

    public PremiumPlanCode getPlanCode() {
        return planCode;
    }

    public void setPlanCode(PremiumPlanCode planCode) {
        this.planCode = planCode;
    }
}
