package com.aerotravel.flightticketbooking.services.servicesimpl;


import com.aerotravel.flightticketbooking.constants.Constants;
import com.aerotravel.flightticketbooking.model.Role;
import com.aerotravel.flightticketbooking.model.User;
import com.aerotravel.flightticketbooking.repository.UserRepository;
import com.aerotravel.flightticketbooking.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getAuthorities(user));
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String[] userRoles = user.getRoles().stream().map((role) -> role.getName()).toArray(String[]::new);
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }

    @Override
    public boolean signUp(String firstName, String middleName, String lastName, String username, String email, String password, Model model) {
        Optional<User> existingUser = userRepository.findByUsername(email);
        if(existingUser!=null){
            model.addAttribute("error", true);
            return false;
        }
        User user = new User();
        user.setFirstname(firstName);
        user.setMiddlename(middleName);
        user.setLastname(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        List<Role> roles = new ArrayList<Role>();
        Role role = new Role();
        role.setId(Constants.USER_ROLE);
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return true;
    }
}
