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
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public String addUser(@RequestBody Map<String,Object> map) {
        return userService.addUser(map.get("email").toString(), map.get("role").toString());
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public List<User> getAllUsers() {
        return userService.loadUsers();
    }

    // admin or super admin can change role of a user (NEED TO CHANGE TO ONLY SUPERADMIN)
    // also change userRole as RequestBody maybe
    @GetMapping("/access/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') or hasAuthority('ROLE_ADMIN')")
    public String changeRole(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
        return userService.changeRole(userId, userRole, principal);
    }

    @GetMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public String deactivateAccount(@PathVariable int userId) {
        return userService.deleteUser(userId);
    }

}