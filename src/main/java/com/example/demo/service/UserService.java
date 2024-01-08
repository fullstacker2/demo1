package com.example.demo.service;

import com.example.demo.common.UserConstant;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
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
        String password = AutoPassword();  // make it randomly generated
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

    public String updatePassword(int user_id, String password, String first_name, String last_name) {
        Optional<User> optUser = userRepository.findById(user_id);
        if(optUser.isEmpty()) {
            throw new RuntimeException("given User doesn't exist");
        }

        User user = optUser.get();
        String encryptedPwd = passwordEncoder.encode(password);
        user.setPassword(encryptedPwd);
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        userRepository.save(user);
        return "Successfully updated password for "+ first_name + " " + last_name;
    }

    public String changeRole(int user_id, String user_role, Principal principal)
    {
        Optional<User> optUser = userRepository.findById(user_id);
        if(optUser.isEmpty()) {
            throw new RuntimeException("given User doesn't exist");
        }

        User user = optUser.get();
        //List<String> activeRoles = getRolesByLoggedInUser(principal);
        String newRole = user.getRoles() + "," + user_role;
        user.setRoles(newRole);
        userRepository.save(user);
        return "Hi " + user.getEmail() + ", new Role assigned to you by " + principal.getName();
    }

    public String deleteUser(int user_id) {
        Optional<User> optUser = userRepository.findById(user_id);
        if(optUser.isEmpty()) {
            throw new RuntimeException("given User doesn't exist");
        }
        userRepository.deleteById(user_id);
        return "User with id: "+ user_id + " is deleted";
    }

    private String AutoPassword() {
        String upper="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower="abcdefghijklmnopqrstuvwxyz";
        String num="0123456789";
        String specialChars="<>,. ?/}]{]+_-) (*&^%$#@!=";
        String combination=upper+lower+specialChars+num;
        int len = 8;
        char[] password=new char[len];
        Random r = new Random();
        for(int i=0;i<len;i++) {
            password[i]=combination.charAt(r.nextInt(combination.length()));
        }
        return new String(password);
    }

    /*

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

     */
}
