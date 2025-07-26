package resilience.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    // Value 타입을 Object로 지정하여 다양한 이벤트를 보낼 수 있도록 함
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object payload) {
        kafkaTemplate.send(topic, payload);
    }
}