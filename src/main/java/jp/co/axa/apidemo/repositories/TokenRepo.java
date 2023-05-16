package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface TokenRepo extends JpaRepository<Token, Long> {
    Token findTokenByTokenValue(String tokenValue);
}
