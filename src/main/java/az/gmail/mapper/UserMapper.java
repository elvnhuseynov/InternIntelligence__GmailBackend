package az.gmail.mapper;

import az.gmail.entity.User;
import az.gmail.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
