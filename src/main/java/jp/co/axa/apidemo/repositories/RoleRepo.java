package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepo extends JpaRepository<Role, UUID> {
    Role findRoleByName(String name);
}
