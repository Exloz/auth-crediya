package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.register.UserRegisterReq;
import co.com.bancolombia.api.dto.register.UserRegisterRes;
import co.com.bancolombia.api.dto.user.UserInfoRes;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "role", target = "roleId")
//    @Mapping(target = "password", ignore = true)
    User toModel(UserRegisterReq usrReq);

    UserRegisterRes toResponse(User user);

    UserInfoRes toUserInfoResponse(User user);
}
