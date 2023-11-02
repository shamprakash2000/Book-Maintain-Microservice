package com.example.User_Service.Controller;


import com.example.User_Service.Model.User;
import com.example.User_Service.Response.Response;
import com.example.User_Service.Service.TokenBlacklistService;
import com.example.User_Service.Service.UserService;
import com.example.User_Service.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;



@RestController
@RequestMapping("userService/api")
public class UserController {

    @Autowired
    private UserService userService;



    @Autowired
    private UserService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    @GetMapping("/health")
    public ResponseEntity<?> health(HttpServletRequest request) {
        String bearer=request.getHeader("Authorization");
        String s=bearer.substring(7);
        String str = jwtUtil.extractAllClaims(s).get("role",String.class);
        System.out.println("line 45 role: "+str);
//        tokenBlacklistService.blacklistToken(s);
//        tokenBlacklistService.isTokenBlacklisted(s);
        System.out.println("line 53");
        if (str.equals("ADMIN")) {

            return ResponseEntity.status(HttpStatus.OK).body("User Service Up and Running...!");
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to access");
        }

    }


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        return userService.signUp(user);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws Exception {

        return userService.login(user);
    }


    @GetMapping("/fetchMyDetails")
    public ResponseEntity<?> fetchUserDetails(HttpServletRequest request){
//        String bearer=request.getHeader("Authorization");
//        String s=bearer.substring(7);
//        tokenBlacklistService.isTokenBlacklisted(s);
        return userService.fetchUserDetails(request);
    }

    @PatchMapping("/updatePhoneNumber")
    public ResponseEntity<?> updatePhoneNumber(@RequestBody User user,HttpServletRequest request){
        return userService.updatePhoneNumber(user,request);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        return userService.logout(request);
    }

    @PatchMapping("/deleteUser")
    public ResponseEntity deleteUser(@RequestBody User user,HttpServletRequest request){
        return userService.deleteUser(user,request);
    }

    //get user details


    //update user details


    //delete(based on some scenario)
}
