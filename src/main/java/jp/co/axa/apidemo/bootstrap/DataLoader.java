package jp.co.axa.apidemo.bootstrap;

import jp.co.axa.apidemo.entities.Authority;
import jp.co.axa.apidemo.entities.Role;
import jp.co.axa.apidemo.entities.UserEntity;
import jp.co.axa.apidemo.repositories.AuthRepo;
import jp.co.axa.apidemo.repositories.RoleRepo;
import jp.co.axa.apidemo.repositories.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
@Transactional
public class DataLoader implements CommandLineRunner {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final AuthRepo authRepo;
    private final Environment environment;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DataLoader(UserRepo userRepo, RoleRepo roleRepo, AuthRepo authRepo, Environment environment, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.authRepo = authRepo;
        this.environment = environment;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        UserEntity userEntity = new UserEntity(
                "jdoe@gmail.com",
                "jdoe",
                bCryptPasswordEncoder.encode("password"),
                new HashSet<>(),
                true);

        Authority readOrder = authRepo.save(new Authority("master.read"));
        Authority createOrder = authRepo.save(new Authority("master.create"));
        Authority updateOrder = authRepo.save(new Authority("master.update"));
        Authority deleteOrder = authRepo.save(new Authority("master.delete"));

        Role superRole = new Role("Super");
        superRole.getPermission().add(readOrder);
        superRole.getPermission().add(createOrder);
        superRole.getPermission().add(updateOrder);
        superRole.getPermission().add(deleteOrder);

        superRole = roleRepo.save(superRole);

        Role adminRole = new Role("Admin");
        adminRole.getPermission().add(readOrder);
        adminRole.getPermission().add(updateOrder);
        adminRole.getPermission().add(createOrder);

        roleRepo.save(adminRole);

        Role hrRole = new Role("HR");
        hrRole.getPermission().add(readOrder);
        hrRole.getPermission().add(updateOrder);

        roleRepo.save(hrRole);

        Role managerRole = new Role("Manager");
        managerRole.getPermission().add(readOrder);
        managerRole.getPermission().add(updateOrder);

        roleRepo.save(managerRole);

        Role userRole = new Role("User");
        userRole.getPermission().add(readOrder);

        roleRepo.save(userRole);

        userEntity.getRoles().add(roleRepo.findRoleByName("Super"));
        userRepo.save(userEntity);

        List<String> urls = Arrays.asList(Objects.requireNonNull(environment.getProperty("public.urls")).split(","));
        System.out.println("Public Endpoints:");
        urls.forEach(System.out::println);
    }


}
