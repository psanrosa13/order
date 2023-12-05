package com.example.order.domain;

import java.util.UUID;

public record Order(UUID orderId, boolean paymentAuthorized, String status) {
}
