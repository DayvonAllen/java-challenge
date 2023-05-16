package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.api.v1.domain.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto register(UserDto userDto);
    UserDto findUserByUsername(String username);
    UserDto findUserByEmail(String email);
}
