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

    @PostMapping("/updatePassword/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String updatePassword(@PathVariable int user_id, @RequestBody Map<String,Object> map) {
        return userService.updatePassword(user_id, map.get("password").toString(), map.get("first_name").toString(), map.get("last_name").toString());
    }

    // admin or super admin can change role of a user (NEED TO CHANGE TO ONLY SUPERADMIN)
    // also change userRole as RequestBody maybe
    @PostMapping("/changeRole/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public String changeRole(@PathVariable int user_id, @RequestBody Map<String,Object> map, Principal principal) {
        return userService.changeRole(user_id, map.get("role").toString(), principal);
    }

    @GetMapping("/delete/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public String deactivateAccount(@PathVariable int user_id) {
        return userService.deleteUser(user_id);
    }

}