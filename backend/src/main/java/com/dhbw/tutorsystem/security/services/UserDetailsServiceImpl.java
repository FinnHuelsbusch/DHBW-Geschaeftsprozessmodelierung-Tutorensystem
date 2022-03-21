package com.dhbw.tutorsystem.security.services;


import com.dhbw.tutorsystem.user.User;
import com.dhbw.tutorsystem.user.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // note: in this case, the username is an email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("User not found with email {}", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            });
                

        return UserDetailsImpl.build(user);
    }
}
