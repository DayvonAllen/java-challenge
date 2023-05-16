package jp.co.axa.apidemo.api.v1.mapper;

import jp.co.axa.apidemo.api.v1.domain.UserDto;
import jp.co.axa.apidemo.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserDto userToUserDtoMapper(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        // sets the password field to null when sending a response back to the user
        userEntity.setPassword((null));
        return new UserDto(userEntity.getId(), userEntity.getEmail(),
                userEntity.getUsername(), userEntity.getPassword(), userEntity.getRoles(),
                userEntity.getNotLocked());
    }

    @Override
    public UserEntity userDtoToUserMapper(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return new UserEntity(userDto.getUserId(), userDto.getEmail(),
                userDto.getUsername(), userDto.getPassword(), userDto.getRoles(),
                userDto.getNotLocked());
    }
}
