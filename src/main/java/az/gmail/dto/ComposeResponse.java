package az.gmail.dto;

import az.gmail.enums.ComposeStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComposeResponse {
    private Long id;
    private ComposeStatus status;
}