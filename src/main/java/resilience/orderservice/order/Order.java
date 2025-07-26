package resilience.orderservice.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId; // 어떤 상품을 주문했는지 ID로 관리
    private int quantity;
    private LocalDateTime orderDate;
    private String status;

    public Order(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderDate = LocalDateTime.now();
        this.status = "COMPLETED"; // 주문과 동시에 완료 처리
    }
}