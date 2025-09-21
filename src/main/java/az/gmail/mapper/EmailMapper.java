package az.gmail.mapper;

import az.gmail.dto.*;
import az.gmail.entity.Email;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EmailRecipientMapper.class})
public interface EmailMapper {

    // Email → EmailListItem (Inbox və Starred üçün)
    @Mapping(target = "snippet", expression = "java(email.getBody() == null ? \"\" : email.getBody().substring(0, Math.min(120, email.getBody().length())))")
    @Mapping(target = "senderName", source = "sender.fullName")
    @Mapping(target = "starred", expression = "java(starred)")
    EmailListItem toEmailListItem(Email email, @Context boolean starred);

    // Email → EmailDetailResponse (detal üçün)
    @Mapping(target = "senderName", source = "sender.fullName")
    @Mapping(target = "starred", expression = "java(starred)")
    @Mapping(target = "to", ignore = true)
    @Mapping(target = "cc", ignore = true)
    @Mapping(target = "bcc", ignore = true)
    EmailDetailResponse toEmailDetailResponse(Email email, @Context boolean starred);

    // ComposeRequest → Email (DB-ə yazmaq üçün)
    @Mapping(target = "subject", expression = "java(request.getSubject() != null ? request.getSubject() : \"\")")
    @Mapping(target = "body", source = "body")
    @Mapping(target = "status", ignore = true) // service-də set olunur
    @Mapping(target = "sender", ignore = true) // service-də set olunur
    @Mapping(target = "recipients", ignore = true)
    Email toEmail(ComposeRequest request);

    // Email → ComposeResponse (cavab üçün)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "status", expression = "java(email.getStatus().name())")
    ComposeResponse toComposeResponse(Email email);
}