package com.example.User_Service.Controller;


import com.example.User_Service.Model.User;
import com.example.User_Service.Response.Response;
import com.example.User_Service.Service.UserService;
import com.example.User_Service.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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



    @GetMapping("/health")
    public ResponseEntity<?> health(HttpServletRequest request) {

        System.out.println("health"+request.getHeader("Authorization"));
//        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
//        System.out.println(hashedPassword);
        String bearer=request.getHeader("Authorization");

//        return ResponseEntity.status(HttpStatus.OK).body("User Service up and Running");
        //Optional<User> user= Optional.ofNullable((User) jwtUtil.extractAllClaims(bearer.substring(7)).get("user"));
        String s=bearer.substring(7);
        String str = jwtUtil.extractAllClaims(s).get("userType",String.class);
        return ResponseEntity.status(HttpStatus.OK).body(str);
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        return userService.signUp(user);
    }


    @PostMapping("/auth")
    public ResponseEntity auth(@RequestBody User user) throws Exception {

        return userService.login(user);
    }
}
