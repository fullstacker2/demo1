package com.example.demo.service;

import com.example.demo.common.UserConstant;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
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
        SecureRandom random = new SecureRandom();

        // Define character sets
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_+=<>?";

        // Initialize variables
        StringBuilder passwordBuilder = new StringBuilder();
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        // Add at least one uppercase character
        passwordBuilder.append(upperCase.charAt(random.nextInt(upperCase.length())));
        hasUpperCase = true;

        // Add at least one lowercase character
        passwordBuilder.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        hasLowerCase = true;

        // Add at least one digit
        passwordBuilder.append(digits.charAt(random.nextInt(digits.length())));
        hasDigit = true;

        // Add at least one special character
        passwordBuilder.append(specialChars.charAt(random.nextInt(specialChars.length())));
        hasSpecialChar = true;

        // Fill the remaining characters
        for (int i = 4; i < 12; i++) {
            int choice = random.nextInt(4); // 0 for uppercase, 1 for lowercase, 2 for digit, 3 for special char

            switch (choice) {
                case 0:
                    passwordBuilder.append(upperCase.charAt(random.nextInt(upperCase.length())));
                    hasUpperCase = true;
                    break;
                case 1:
                    passwordBuilder.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
                    hasLowerCase = true;
                    break;
                case 2:
                    passwordBuilder.append(digits.charAt(random.nextInt(digits.length())));
                    hasDigit = true;
                    break;
                case 3:
                    passwordBuilder.append(specialChars.charAt(random.nextInt(specialChars.length())));
                    hasSpecialChar = true;
                    break;
            }
        }

        // Shuffle the characters in the password
        char[] passwordArray = passwordBuilder.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }

        // Ensure all criteria are met
        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            // Regenerate the password if any criteria is not met
            return AutoPassword();
        }
        return new String(passwordArray);
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
