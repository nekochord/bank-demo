package com.demo.user.service;

import com.demo.user.entity.User;
import com.demo.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class UserService implements UserDetailsService {
    public static GrantedAuthority COMMON_AUTHORITY = new SimpleGrantedAuthority("COMMON");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identityNumber) throws UsernameNotFoundException {
        User user = userRepository.findByIdentityNumber(identityNumber)
                .orElseThrow(() -> new UsernameNotFoundException("no such user, identityNumber=" + identityNumber));
        org.springframework.security.core.userdetails.User userDetail =
                new org.springframework.security.core.userdetails.User(user.getIdentityNumber(), user.getPassword(), Collections.singletonList(COMMON_AUTHORITY));
        return userDetail;
    }
}
