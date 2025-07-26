package resilience.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import resilience.orderservice.outbox.OutboxEvent;
import resilience.orderservice.outbox.OutboxEventRepository;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {
    private final OutboxEventRepository outboxRepository;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void relayOutboxMessages() {
        List<OutboxEvent> pendingMessages = outboxRepository.findByStatus(OutboxEvent.EventStatus.PENDING);
        if (pendingMessages.isEmpty()) return;

        log.info("📬 [Relay] 아웃박스에서 {}개의 이벤트를 발견! 릴레이를 시작합니다.", pendingMessages.size());

        for (OutboxEvent event : pendingMessages) {
            event.changeStatus(OutboxEvent.EventStatus.PROCESSING);
            // 상태를 먼저 저장하여 다른 스케줄러가 중복으로 가져가지 않도록 합니다.
            outboxRepository.save(event);

            try {
                // 이벤트 타입에 따라 다른 토픽으로 메시지를 발행하도록 수정
                if ("STOCK_DECREASED".equals(event.getEventType())) {
                    kafkaProducerService.send("stock-decrease-topic", event.getPayload());
                    log.info("✅ [Relay] '재고 감소' 이벤트 발행 성공 (ID: {})", event.getId());
                } else if ("STOCK_INCREASED".equals(event.getEventType())) {
                    // '재고 증가' 보상 이벤트를 처리하는 로직 추가
                    kafkaProducerService.send("stock-increase-topic", event.getPayload());
                    log.warn("🚨 [Relay] '재고 증가' 보상 이벤트 발행 성공 (ID: {})", event.getId());
                }

                event.changeStatus(OutboxEvent.EventStatus.PUBLISHED);
                outboxRepository.save(event);
            } catch (Exception e) {
                log.error("❌ [Relay] 이벤트 발행 실패 ID: {}", event.getId(), e);
                event.changeStatus(OutboxEvent.EventStatus.FAILED);
                outboxRepository.save(event);
            }
        }
    }
}