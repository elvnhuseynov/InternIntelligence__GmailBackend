package az.gmail.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "email_stars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailStar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "email_id")
    private Email email;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Instant createdAt;
}
