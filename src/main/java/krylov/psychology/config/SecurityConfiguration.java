package krylov.psychology.config;

import krylov.psychology.security.AdminDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AdminDetailsServiceImpl adminDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();


        // Set permissions on endpoints
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/h2console/**").permitAll()
                .antMatchers("/**").permitAll()
//                .antMatchers("/admin/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/**").authenticated()
                .and().formLogin().permitAll();



        // for exposure h2 dataBase
        http.headers().frameOptions().disable();
    }
}
