package kz.edev.order.controller;
import kz.edev.order.entity.Order;
import kz.edev.order.entity.OrderBody;
import kz.edev.order.service.OrderService;
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
    private OrderService orderService;

    @PostMapping("")
    public ResponseEntity makeOrder(@RequestBody OrderBody body) {
        return ResponseEntity.ok(orderService.create(body));
    }
}
