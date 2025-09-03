package az.gmail.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "email_label_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailLabelMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "email_id")
    private Email email;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "label_id")
    private EmailLabel label;
}
