package az.gmail.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailListItem {
    private Long id;
    private String subject;
    private String snippet;
    private String senderName;
    private Instant createdAt;
    private boolean isRead;
    private boolean isStarred;
}
