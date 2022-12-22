package krylov.psychology.security;

import krylov.psychology.security.jwt.JwtUserFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class AdminDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if (login != "KrylovaDi") {
            System.out.println("User with email = " + login + " not exist!");
            throw new UsernameNotFoundException("User with login = " + login + " not exist!");
        }
        System.out.println("User with login = " + login + " found");
        return JwtUserFactory.create(login);
    }
}
