package com.example.order.controller;

import com.example.order.domain.NewOrder;
import com.example.order.domain.Order;
import com.example.order.service.OrderService;
import com.example.order.service.PaymentService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/orders")
public class OrderController {


    @Autowired
    private PaymentService paymentService;


    @Autowired
    private OrderService orderService;


    @PostMapping("/bulkhead")
    @Bulkhead(name = "payment", type = Bulkhead.Type.SEMAPHORE)
    public ResponseEntity<?> createWithBulkhead(@RequestBody NewOrder newOrder) throws ExecutionException, InterruptedException {

        var authorized = paymentService.authorize(newOrder);

       if (authorized){
           Order order = orderService.save(newOrder);
           return ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order);
       }

        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }

    @PostMapping("/ratelimiter")
    @RateLimiter(name = "payment")
    public ResponseEntity<?> createWithRateLimiter(@RequestBody NewOrder newOrder) throws ExecutionException, InterruptedException {

        var authorized = paymentService.authorize(newOrder);

        if (authorized){
            Order order = orderService.save(newOrder);
            return ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order);
        }

        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }

    @PostMapping("/timelimiter1")
    @TimeLimiter(name = "payment")
    public CompletableFuture<ResponseEntity<?>> createWithTimeLimiter1(@RequestBody NewOrder newOrder){

        var authorized = paymentService.authorize(newOrder);

        if (authorized){
            Order order = orderService.save(newOrder);
            return CompletableFuture.supplyAsync(() -> ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order));
        }

        return CompletableFuture.supplyAsync(() -> ResponseEntity.internalServerError().body("Error in Create new Order"));
    }

    @PostMapping("/timelimiter2")
    public ResponseEntity<?> createWithTimeLimiter2(@RequestBody NewOrder newOrder) throws ExecutionException, InterruptedException {

        var authorized = paymentService.authorizeTimeLimiter(newOrder);

        if (authorized.get()){
            Order order = orderService.save(newOrder);
            return ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order);
        }

        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }

    @PostMapping("/circuitbreaker")
    public ResponseEntity<?> createWithCircuitBreaker(@RequestBody NewOrder newOrder) throws ExecutionException, InterruptedException {

        var authorized = paymentService.authorizeWithCircuitBreaker(newOrder);

        if (authorized){
            Order order = orderService.save(newOrder);
            return ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order);
        }

        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }

    @PostMapping("/retry")
    public ResponseEntity<?> createWithRetry(@RequestBody NewOrder newOrder) throws ExecutionException, InterruptedException {

        var authorized = paymentService.authorizeWithRetry(newOrder);

        if (authorized){
            Order order = orderService.save(newOrder);
            return ResponseEntity.created(URI.create("http://localhost:8080"+order.orderId())).body(order);
        }

        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }
}
