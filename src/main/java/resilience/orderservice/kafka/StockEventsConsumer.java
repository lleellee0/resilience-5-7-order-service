package resilience.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import resilience.orderservice.product.Product;
import resilience.orderservice.product.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventsConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "stock-decrease-topic", groupId = "stock-group")
    @Transactional
    public void listenStockDecrease(StockDecreaseEvent event) {
        log.info("🚚 [Consumer] '재고 감소' 이벤트 수신. 상품 ID: {}, 요청 버전: {}", event.getProductId(), event.getProductVersion());

        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new RuntimeException("이벤트에 해당하는 상품을 찾을 수 없습니다."));

        log.info("현재 DB 상품 버전: {}", product.getVersion());

        // 버전을 체크하지 않고 재고 감소 처리
        product.decreaseStock(event.getQuantity());
        log.info("✅ 재고 감소 처리 완료. 상품 ID: {}, 변경 후 재고: {}, 변경 후 버전: {}",
                product.getId(), product.getStock(), product.getVersion() + 1);

        // [핵심] 이벤트에 담긴 버전과 DB의 상품 버전이 일치할 때만 재고를 감소시킵니다.
//        if (event.getProductVersion().equals(product.getVersion())) {
//            product.decreaseStock(event.getQuantity());
//            log.info("✅ 재고 감소 처리 완료. 상품 ID: {}, 변경 후 재고: {}, 변경 후 버전: {}",
//                    product.getId(), product.getStock(), product.getVersion() + 1);
//        } else {
//            // 버전이 일치하지 않으면, 이미 다른 트랜잭션(이전에 도착한 동일 메시지 등)에 의해
//            // 처리된 요청으로 간주하고 현재 요청을 무시합니다.
//            log.warn("⚠️ 중복 이벤트 감지. 처리를 건너뜁니다. 요청 버전: {}, DB 버전: {}",
//                    event.getProductVersion(), product.getVersion());
//        }
    }
}