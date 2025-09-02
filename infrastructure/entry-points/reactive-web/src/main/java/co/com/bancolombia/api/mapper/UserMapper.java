package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "role", target = "roleId")
    User toModel(UserRegisterReq usrReq);

    UserRegisterRes toResponse(User user);
}
