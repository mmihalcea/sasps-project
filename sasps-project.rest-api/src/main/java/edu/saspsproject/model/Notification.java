package edu.saspsproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    public enum NotificationMethod {
        EMAIL,
        SMS,
        NONE
    }

    public enum NotificationType {
        CONFIRMATION,
        REMINDER,
        CANCELLATION,
        WELCOME,
        ANNOUNCEMENT
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        SKIPPED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private Long appointmentId;

    private Long institutionId;

    private String recipientEmail;

    private String recipientPhone;

    @Enumerated(EnumType.STRING)
    private NotificationMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }
}
