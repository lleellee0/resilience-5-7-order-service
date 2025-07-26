package resilience.orderservice.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long stock;

    @Version
    private Long version; // 낙관적 락을 위한 버전 필드

    public Product(String name, Long stock) {
        this.name = name;
        this.stock = stock;
    }

    public void decreaseStock(Long quantity) {
        if (this.stock < quantity) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        this.stock -= quantity;
    }
}