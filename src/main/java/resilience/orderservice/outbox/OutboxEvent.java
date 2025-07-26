package resilience.orderservice.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@NoArgsConstructor
public class OutboxEvent {
    public enum EventStatus { PENDING, PROCESSING, PUBLISHED, FAILED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventType;
    @Column(columnDefinition = "TEXT")
    private String payload;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public OutboxEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = EventStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    public void changeStatus(EventStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}