package az.gmail.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "email_labels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // INBOX, STARRED, SENT, DRAFT

    private String name;
}
