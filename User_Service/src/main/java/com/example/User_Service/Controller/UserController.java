package com.example.User_Service.Controller;

import com.example.User_Service.Model.User;
import com.example.User_Service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("userService/api")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private UserService userDetailsService;


    @GetMapping("/health")
    public ResponseEntity<?> health(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body("User Service Up and Running...!");

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        return userService.signUp(user);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user)  {
        return userService.login(user);
    }


    @GetMapping("/fetchMyDetails")
    public ResponseEntity<?> fetchUserDetails(HttpServletRequest request) {
        return userService.fetchUserDetails(request);
    }

    @PatchMapping("/updatePhoneNumber")
    public ResponseEntity<?> updatePhoneNumber(@RequestBody User user, HttpServletRequest request) {
        return userService.updatePhoneNumber(user, request);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        return userService.logout(request);
    }

    @PatchMapping("/deleteUser")
    public ResponseEntity deleteUser(@RequestBody User user, HttpServletRequest request) {
        return userService.deleteUser(user, request);
    }

}
