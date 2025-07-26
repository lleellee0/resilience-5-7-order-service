package resilience.orderservice.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import resilience.orderservice.kafka.KafkaProducerService;
import resilience.orderservice.kafka.StockDecreaseEvent;
import resilience.orderservice.product.Product;
import resilience.orderservice.product.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void placeOrder(OrderRequestDto requestDto) {
        // 1. 상품을 조회하여 현재 버전을 확인합니다.
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        log.info("✅ 주문 요청 접수. 상품 ID: {}, 현재 버전: {}", product.getId(), product.getVersion());

        // 2. 주문 정보를 데이터베이스에 저장합니다.
        Order order = new Order(requestDto.getProductId(), requestDto.getQuantity());
        orderRepository.save(order);

        // 3. 재고 감소 이벤트를 Kafka에 발행합니다. (조회한 버전 정보를 포함)
        StockDecreaseEvent event = new StockDecreaseEvent(
                product.getId(),
                (long) requestDto.getQuantity(),
                product.getVersion() // 현재 조회한 버전을 이벤트에 담습니다.
        );
        kafkaProducerService.send("stock-decrease-topic", event);
        log.info("📤 '재고 감소' 이벤트 발행 완료. Topic: stock-decrease-topic");
    }
}