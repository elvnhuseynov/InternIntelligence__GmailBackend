package az.gmail.mapper;

import az.gmail.entity.EmailRecipient;
import az.gmail.dto.EmailRecipientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface EmailRecipientMapper {
    @Mapping(target = "recipientEmail", source = "recipient.email")
    @Mapping(target = "recipientName", source = "recipient.fullName")
    EmailRecipientDto toDto(EmailRecipient recipient);
}
