package com.example.demo.service;

import com.example.demo.common.UserConstant;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String addUser(String email, String role) {
        User newUser = new User();
        newUser.setEmail(email);
        String password = "XhjE3_P&";  // make it randomly generated
        String encryptedPwd = passwordEncoder.encode(password);
        newUser.setPassword(encryptedPwd);
        newUser.setRoles(role);
        newUser.setActive(true);
        userRepository.save((newUser));

        return "New account created, email: " + newUser.getEmail() + ",  password: " + password;
    }

    public List<User> loadUsers() {
        return userRepository.findAll();
    }

    public String changeRole(int userId, String userRole, Principal principal)
    {
        Optional<User> optUser = userRepository.findById(userId);
        if(optUser.isEmpty()) {
            throw new RuntimeException("given User doesn't exist");
        }

        User user = optUser.get();
        List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole;
        if (activeRoles.contains(userRole)) {
            newRole = user.getRoles() + "," + userRole;
            user.setRoles(newRole);
        }
        userRepository.save(user);
        return "Hi " + user.getEmail() + ", new Role assigned to you by " + principal.getName();
    }

    public String deleteUser(int userId) {
        // write your logic
        return "User with id: "+ userId + "is deleted";
    }

    private List<String> getRolesByLoggedInUser(Principal principal) {
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(",")).toList();
        if (assignRoles.contains("ROLE_SUPERADMIN")) {
            return Arrays.stream(UserConstant.SUPERADMIN_ACCESS).collect(Collectors.toList());
        }
        if (assignRoles.contains("ROLE_ADMIN")) {
            return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private User getLoggedInUser(Principal principal) {
        return userRepository.findByEmail(principal.getName()).get();
    }
}
