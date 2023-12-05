package com.example.order.service;

import com.example.order.domain.NewOrder;
import com.example.order.domain.PaymentDetails;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {


    @Value("${payments.service.url}")
    private String paymentsServiceUrl;

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    @RateLimiter(name = "payment")
    @Bulkhead(name = "payment", fallbackMethod = "fallback")
    @Retry(name = "payment")
    @TimeLimiter(name = "payment")
    public boolean authorize(NewOrder newOrder){

        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/authorize";

        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return Boolean.parseBoolean(authorized);
    }
}
