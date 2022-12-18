package krylov.psychology.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminDetailsServiceImpl implements UserDetailsService {
    @Value(value = "${PASSWORD}")
    private String password;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("admin"));
        UserDetails userDetails = User.withUsername("KrylovaDi")
                .password(password)
                .authorities(authorities)
                .build();
        return userDetails;
    }
}
