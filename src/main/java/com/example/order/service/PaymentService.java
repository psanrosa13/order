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

import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {


    @Value("${payments.service.url}")
    private String paymentsServiceUrl;

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    public Boolean authorizeWithCircuitBreaker(NewOrder newOrder) {
        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/process";

        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return Boolean.parseBoolean(authorized);
    }

    @Retry(name = "payment", fallbackMethod = "fallback")
    public Boolean authorizeWithRetry(NewOrder newOrder) {
        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/process";
        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return Boolean.parseBoolean(authorized);
    }

    @TimeLimiter(name = "payment")
    public CompletableFuture<Boolean> authorizeTimeLimiter(NewOrder newOrder) {
        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/process";
        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return CompletableFuture.supplyAsync(() -> Boolean.parseBoolean(authorized));
    }

    public Boolean authorize(NewOrder newOrder) {
        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/process";
        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return Boolean.parseBoolean(authorized);
    }


    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    @RateLimiter(name = "payment")
    @Bulkhead(name = "payment")
    @Retry(name = "payment")
    @TimeLimiter(name = "payment")
    public CompletableFuture<Boolean> authorizeAll(NewOrder newOrder) {
        PaymentDetails paymentDetails = new PaymentDetails(newOrder.paymentType(), newOrder.paymentValue().toString() );

        String url = paymentsServiceUrl+"/api/payments/process";
        String authorized = restTemplate.postForObject(url, paymentDetails, String.class);

        return CompletableFuture.supplyAsync(() -> Boolean.parseBoolean(authorized));
    }

    public Boolean fallback(NewOrder newOrder, Exception exception){
        return Boolean.FALSE;
    }
}
