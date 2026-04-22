package com.github.TamNguyen.Zob.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqMomoIpnDTO {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private Integer resultCode;
    private String message;
    private String orderInfo;
    private String orderType;
    private Long transId;
    private String payType;
    private Long responseTime;
    private String extraData;
    private String signature;
}
