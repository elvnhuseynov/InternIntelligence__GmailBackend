package az.gmail.entity;

import az.gmail.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "sender_id")
    private User sender;

    private String subject;

    @Lob
    private String body;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private EmailStatus status = EmailStatus.SENT;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "email", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailRecipient> recipients = new ArrayList<>();
}

