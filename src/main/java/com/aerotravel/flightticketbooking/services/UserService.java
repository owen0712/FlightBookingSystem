package com.aerotravel.flightticketbooking.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.aerotravel.flightticketbooking.model.User;

public interface UserService extends UserDetailsService {
    public abstract User findByUsername(String username);
}
