package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.api.v1.domain.UserDto;
import jp.co.axa.apidemo.api.v1.mapper.UserMapper;
import jp.co.axa.apidemo.entities.Role;
import jp.co.axa.apidemo.entities.UserEntity;
import jp.co.axa.apidemo.exceptions.AppException;
import jp.co.axa.apidemo.exceptions.EmailAlreadyExistException;
import jp.co.axa.apidemo.exceptions.UsernameAlreadyExistsException;
import jp.co.axa.apidemo.repositories.RoleRepo;
import jp.co.axa.apidemo.repositories.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepo roleRepo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LoginAttemptService loginAttemptService;

    public UserServiceImpl(UserRepo userRepo, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepo roleRepo, LoginAttemptService loginAttemptService) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepo = roleRepo;
        this.loginAttemptService = loginAttemptService;
    }

    @Value(("${token.header}"))
    private String tokenHeader;

    @Override
    public UserDto register(UserDto userDto) {
        if (userDto == null || !validUserDetail(userDto)) {
            logger.error("Error processing data in register method!");
            throw new AppException("Error processing data");
        }
        if (verifyUsername(userDto.getUsername()) && verifyEmail(userDto.getEmail())) {
            UserDto createdUser = userCreator(userDto);
            HashSet<Role> roles = new HashSet<>();
            // giving super role by default so all endpoints can be tested
            roles.add(roleRepo.findRoleByName("Super"));
            userDto.setRoles(roles);
            System.out.println(createdUser);
            UserEntity userEntity = userRepo.save(userMapper.userDtoToUserMapper(createdUser));
            System.out.println(userEntity);
            logger.info("User successfully created!");
            return userMapper.userToUserDtoMapper(userEntity);
        }
        throw new AppException("Couldn't register user");
    }

    @Override
    public UserDto findUserByUsername(String username) {
        logger.info("Searching for user by the username " + username + " in the find by username method!");
        UserEntity userEntity = userRepo.findUserEntityByUsername(username);
        if(userEntity == null){
            return null;
        }
        return userMapper.userToUserDtoMapper(userEntity);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        logger.info("Searching for user by the email " + email + " in the find by email method!");
        return userMapper.userToUserDtoMapper(userRepo.findUserEntityByEmail(email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findUserEntityByUsername(username);
        if (userEntity == null) {
            logger.error("User with the username " + username + " was not found");
            throw new UsernameNotFoundException("User with the username " + username + " was not found!");
        } else {
            logger.info("User with the username " + username + " was found!");
            if (userAccountLocker(userEntity)) {
                logger.info("User account with username " + username + " is now locked!");
                logger.info("User is unlocked: " + userEntity.getNotLocked());
                return new User(userEntity.getEmail(), userEntity.getPassword(), true, true, true, false, getUserAuthorities(userEntity));
            } else {
                logger.info("Made it past the account locker method! In load by username method!");
                logger.info("User is unlocked: " + userEntity.getNotLocked());
                userRepo.save(userEntity);
                return new User(userEntity.getEmail(), userEntity.getPassword(), true, true, true, true, getUserAuthorities(userEntity));
            }
        }
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(UserEntity user) {
        return user
                .getRoles()
                .stream()
                .map(Role::getPermission)
                .flatMap(Set::stream)
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet());
    }

    private Boolean userAccountLocker(UserEntity user) {
        if (loginAttemptService.exceededMaxAttempts(user.getUsername())) {
            user.setNotLocked(false);
            return true;
        } else {
            user.setNotLocked(true);
            return false;
        }
    }

    private UserDto userCreator(UserDto userDto){
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userDto.setNotLocked(true);

        if(userDto.getRoles().size() == 0){
            userDto.getRoles().add(roleRepo.findRoleByName("Super"));
        }

        return userDto;
    }

    private Boolean verifyUsername(String username) {
        if (findUserByUsername(username) != null) {
            logger.error("Username " + username + " already exists!");
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        logger.info("Username " + username + " not found proceeding to next step of registration!");
        return true;
    }

    private Boolean verifyEmail(String email) {
        if (findUserByEmail(email) != null) {
            logger.error("Email " + email + " already exists!");
            throw new EmailAlreadyExistException("Email already exists");
        }
        logger.info("Email " + email + " not found proceeding to next step of registration!");
        return true;
    }

    private Boolean validUserDetail(UserDto userDto) {
        if (StringUtils.isBlank(userDto.getEmail()) || StringUtils.isBlank(userDto.getUsername())) {
            logger.error("Invalid details provided in the valid user detail method.");
            throw new AppException("Invalid data provided.");
        }
        return true;
    }
}
