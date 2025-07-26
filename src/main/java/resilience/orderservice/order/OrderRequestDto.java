package resilience.orderservice.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {
    private Long productId;
    private int quantity;
}