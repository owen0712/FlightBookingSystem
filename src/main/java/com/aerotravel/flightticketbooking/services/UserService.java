package com.aerotravel.flightticketbooking.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;

import com.aerotravel.flightticketbooking.model.User;

public interface UserService extends UserDetailsService {
    public abstract User findByUsername(String username);

    public abstract boolean signUp(String firstName, String middleName, String lastName, String username, String email, String password, Model model);
}
