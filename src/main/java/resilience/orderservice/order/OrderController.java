package resilience.orderservice.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDto request) {
        orderService.placeOrder(request);
        return ResponseEntity.ok("주문 요청 완료");
    }
}