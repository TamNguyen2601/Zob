package com.github.TamNguyen.Zob.domain;

import java.time.Instant;

import com.github.TamNguyen.Zob.util.constant.PaymentProviderEnum;
import com.github.TamNguyen.Zob.util.constant.PaymentTransactionStatusEnum;
import com.github.TamNguyen.Zob.util.constant.PremiumPlanCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_payment_tx_provider_order_id", columnList = "provider_order_id", unique = true)
})
@Getter
@Setter
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentProviderEnum provider;

    @Enumerated(EnumType.STRING)
    private PremiumPlanCode planCode;

    private long amount;

    @Column(name = "provider_order_id", nullable = false, unique = true)
    private String providerOrderId;

    private String qrCodeUrl;

    @Enumerated(EnumType.STRING)
    private PaymentTransactionStatusEnum status;

    private Instant createdAt;

    private Instant paidAt;
}
