package com.example.vse_back.configuration;

import com.example.vse_back.model.entity.UserEntity;
import com.example.vse_back.model.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity user = userService.getUserById(UUID.fromString(userId));
        return new CustomUserDetails(user, new SimpleGrantedAuthority(user.getRole().getName()));
    }
}
