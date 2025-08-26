package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.UserRegisterReq;
import co.com.bancolombia.api.dto.UserRegisterRes;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User ToModel(UserRegisterReq usrReq);
    UserRegisterRes toResponse(User user);
}
