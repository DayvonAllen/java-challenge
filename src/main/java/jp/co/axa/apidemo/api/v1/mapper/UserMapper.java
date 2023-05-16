package jp.co.axa.apidemo.api.v1.mapper;

import jp.co.axa.apidemo.api.v1.domain.UserDto;
import jp.co.axa.apidemo.entities.UserEntity;

public interface UserMapper {
    UserDto userToUserDtoMapper(UserEntity userEntity);
    UserEntity userDtoToUserMapper(UserDto userDto);
}
