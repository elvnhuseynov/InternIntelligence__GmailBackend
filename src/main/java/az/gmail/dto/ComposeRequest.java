package az.gmail.dto;

import az.gmail.enums.ActionType;
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

    @NotEmpty(message = "Recipient list (to) cannot be empty")
    private List<@Email(message = "Invalid email address") String> to;

    private List<@Email(message = "Invalid email address") String> cc;

    private List<@Email(message = "Invalid email address") String> bcc;

    @NotBlank(message = "Subject cannot be blank")
    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    private String subject;

    @Size(max = 5000, message = "Body cannot exceed 5000 characters")
    private String body;

    @NotNull(message = "Action is required")
    private ActionType action;
}
