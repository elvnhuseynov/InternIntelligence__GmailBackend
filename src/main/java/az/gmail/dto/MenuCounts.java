package az.gmail.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCounts {
    private long inbox;
    private long starred;
    private long sent;
    private long draft;
}
