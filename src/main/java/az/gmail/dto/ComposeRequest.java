package az.gmail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComposeRequest {
    @NotNull
    @NotEmpty
    private List<@Email String> to;
    private List<@Email String> cc;
    private List<@Email String> bcc;
    private String subject;
    private String body;
    @NotBlank
    private String action; // SEND or SAVE
}
