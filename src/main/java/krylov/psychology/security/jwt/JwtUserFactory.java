package krylov.psychology.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtUserFactory {
    @Value(value = "${PASSWORD}")
    private static String password;
    public static JwtUser create(String login) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("admin"));
        return new JwtUser(login, password, authorities);
    }
}
