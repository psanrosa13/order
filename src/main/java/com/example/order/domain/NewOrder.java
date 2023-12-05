package com.example.order.domain;

import java.util.UUID;

public record NewOrder(UUID clientId, String paymentType, Double paymentValue) {
}
