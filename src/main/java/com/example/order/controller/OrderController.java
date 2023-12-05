package com.example.order.controller;

import com.example.order.domain.NewOrder;
import com.example.order.domain.Order;
import com.example.order.service.OrderService;
import com.example.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {


    @Autowired
    private PaymentService paymentService;


    @Autowired
    private OrderService orderService;


    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewOrder newOrder){

        var authorized = paymentService.authorize(newOrder);

       if (authorized){
           Order order = orderService.save(newOrder);
           return ResponseEntity.created(null).body(order);
       }


        return ResponseEntity.internalServerError().body("Error in Create new Order");
    }

}
