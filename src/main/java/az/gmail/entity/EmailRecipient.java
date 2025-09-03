package az.gmail.entity;

import az.gmail.enums.RecipientType;
import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "email_recipients")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmailRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "email_id")
    private Email email;

    @ManyToOne @JoinColumn(name = "recipient_id")
    private User recipient;

    @Enumerated(EnumType.STRING)
    private RecipientType type;
}
