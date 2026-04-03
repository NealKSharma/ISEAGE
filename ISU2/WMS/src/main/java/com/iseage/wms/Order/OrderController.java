package com.iseage.wms.Order;

import com.iseage.wms.Label.LabelGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        if(orderRequest.getOrderItems().isEmpty()){
            return ResponseEntity.badRequest().body("The order has no items!");
        }
        try{
            orderRepo.createOrder(orderRequest.getOrder(), orderRequest.getOrderItems());
            return ResponseEntity.ok("Order Placed Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/complete")
    public ResponseEntity<String> completeOrder(@RequestParam Long oid) {
        try{
            orderRepo.completeOrder(oid);
            return ResponseEntity.ok("Order Completed Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderRequest>> getAllOrders(@RequestParam Long uid){
        try {
            return ResponseEntity.ok(orderRepo.getAllOrders(uid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get")
    public ResponseEntity<OrderRequest> getOrder(@RequestParam Long oid){
        try {
            return ResponseEntity.ok(orderRepo.getOrder(oid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam Long oid){
        try {
            orderRepo.cancelOrder(oid);
            return ResponseEntity.ok().body("Order Cancelled Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}