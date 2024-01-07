package com.example.demo.controller;


import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUser")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addUser(@RequestBody Map<String,Object> map) {
        return userService.addUser(map.get("email").toString(), map.get("role").toString());
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userService.loadUsers();
    }

    @GetMapping("/access/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String changeRole(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
        return userService.changeRole(userId, userRole, principal);
    }

    @GetMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deactivateAccount(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

}