package com.onestep.business_management.Controller;

import com.onestep.business_management.DTO.OrderReportResponse;
import com.onestep.business_management.DTO.OrderRequest;
import com.onestep.business_management.DTO.OrderResponse;
import com.onestep.business_management.DTO.PaymentRequest;
import com.onestep.business_management.Service.OrderService.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrder() {
        try {
            List<OrderResponse> response = orderService.getAllOrders();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse response = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        try {
            OrderResponse response = orderService.getOrderById(orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/payment")
    public ResponseEntity<OrderResponse> updateOrderPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            System.out.println(paymentRequest.toString());
            OrderResponse response = orderService.Payment(paymentRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<OrderReportResponse> getOrderReports(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Set default values if parameters are not provided
            if (startDate == null) {
                startDate = LocalDate.now().minusMonths(1); // Default to one month ago
            }
            if (endDate == null) {
                endDate = LocalDate.now(); // Default to today
            }

            // Ensure endDate is not before startDate
            if (endDate.isBefore(startDate)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Call the service with the provided or default dates
            OrderReportResponse response = orderService.getOrderReports(startDate, endDate);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
