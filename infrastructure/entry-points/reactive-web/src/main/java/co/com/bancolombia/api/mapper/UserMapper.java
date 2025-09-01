package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.AdminUserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roleId", constant = "USER")
    User toModel(UserRegisterReq usrReq);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "baseSalary", ignore = true)
    User toModel(AdminUserRegisterReq adminReq);

    UserRegisterRes toResponse(User user);
}
