package resilience.orderservice.product;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductInitializer {

    // 테스트를 위한 초기 데이터 생성
    @Bean
    public ApplicationRunner applicationRunner(ProductRepository productRepository) {
        return args -> {
            productRepository.save(new Product("멱등성 보장 상품", 100L));
        };
    }

}