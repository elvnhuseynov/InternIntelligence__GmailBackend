package az.gmail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetailResponse {
    private Long id;
    private String subject;
    private String body;
    private String senderName;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private Instant createdAt;

    @JsonProperty("isRead")
    private boolean read;

    @JsonProperty("isStarred")
    private boolean starred;

}
