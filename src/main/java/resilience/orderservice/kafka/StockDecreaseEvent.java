package resilience.orderservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockDecreaseEvent {
    private Long productId;
    private Long quantity;
    private Long productVersion; // 재고 감소 요청 시점의 상품 버전
}