package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthRepo extends JpaRepository<Authority, UUID> {
}
