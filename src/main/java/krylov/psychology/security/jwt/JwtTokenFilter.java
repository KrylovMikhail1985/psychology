package krylov.psychology.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = null;

        // Get token from header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer")) {
            token = header.split(" ")[1].trim();
        }

        // Get token from cookies
        Cookie[] requestCookies = request.getCookies();
        int numberOfCookies = 0;
        try {
            numberOfCookies = requestCookies.length;
        } catch (Exception e) { }
        for (var i = 0; i < numberOfCookies; i++) {
            if (requestCookies[i].getName().equals("auth_token")) {
                token = requestCookies[i].getValue();
            }
        }
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }
        if (!jwtTokenProvider.tokenIsValid(token)) {
            chain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) jwtTokenProvider.getAuthentication(token);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
