package com.example.springjwt_db.controller;

import com.example.springjwt_db.dto.UserResponse;
import com.example.springjwt_db.entity.User;
import com.example.springjwt_db.repository.UserRepository;
import com.example.springjwt_db.security.UserDetailsImpl;
import com.example.springjwt_db.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> ListAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        log.info("Obtendo perfil do usuário atual");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Autenticação obtida do contexto: {}", authentication);
            
            if (authentication != null && authentication.isAuthenticated()) {
                log.info("Principal: {}, Autoridades: {}", 
                         authentication.getPrincipal(), 
                         authentication.getAuthorities());
                
                if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    log.info("UserDetailsImpl obtido com sucesso: {}", userDetails.getUsername());
                    
                    List<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    
                    log.info("Roles do usuário: {}", roles);
                    
                    return ResponseEntity.ok(new UserResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles
                    ));
                } else {
                    log.warn("Principal não é do tipo UserDetailsImpl: {}", authentication.getPrincipal().getClass());
                    
                    // Tentar uma abordagem alternativa usando o nome do usuário
                    String username = authentication.getName();
                    log.info("Tentando buscar usuário pelo username: {}", username);
                    
                    User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
                    
                    List<String> roles = user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList());
                    
                    return ResponseEntity.ok(new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        roles
                    ));
                }
            } else {
                log.error("Autenticação nula ou não autenticada");
                return ResponseEntity.status(401).body("Não autorizado");
            }
        } catch (Exception e) {
            log.error("Erro ao obter perfil do usuário: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Erro ao obter perfil: " + e.getMessage());
        }
    }
}
