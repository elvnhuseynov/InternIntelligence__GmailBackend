package az.gmail.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRecipientDto {

    private String recipientEmail;
    private String recipientName;

}
