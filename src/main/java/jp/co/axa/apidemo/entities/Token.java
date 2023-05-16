package jp.co.axa.apidemo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Lob
    private String tokenValue;
    private String expirationDate;

    public Token() {
    }

    public Token(String tokenValue, String expirationDate) {
        this.tokenValue = tokenValue;
        this.expirationDate = expirationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(id, token.id) &&
                Objects.equals(tokenValue, token.tokenValue) &&
                Objects.equals(expirationDate, token.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tokenValue, expirationDate);
    }
}
