package com.example.springjwt_db.security;

import com.example.springjwt_db.config.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            log.info("URI requisitada: {}", request.getRequestURI());
            
            if (jwt != null) {
                log.info("Token JWT encontrado");
                if (jwtUtils.validateJwtToken(jwt)) {
                    log.info("Token JWT válido");
                    String username = jwtUtils.getUsernameFromToken(jwt);
                    log.info("Username do token: {}", username);
                    
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.info("Usuário carregado com sucesso: {}", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    
                    log.info("Autenticação definida no contexto de segurança");
                } else {
                    log.warn("Token JWT inválido");
                }
            } else {
                log.info("Sem token JWT na requisição");
            }
        } catch (Exception e) {
            log.error("Não foi possível autenticar o usuário: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
