package com.example.User_Service.Service;


import com.example.User_Service.Model.User;
import com.example.User_Service.Repository.UserRepository;
import com.example.User_Service.Response.Response;
import com.example.User_Service.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Lazy
public class UserService{


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    MyUserDetailsService myUserDetailsService;




    public ResponseEntity<?> signUp(User user) {

        user.setLastModifiedDate(LocalDateTime.now());
        user.setUserSignUpDate(LocalDateTime.now());
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        System.out.println(hashedPassword);
        user.setPassword(hashedPassword);
        User savedUser= userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(new Response("User inserted to database successfully", savedUser));
    }

    public ResponseEntity<?> login(User user){
        System.out.println("inside auth");

        try {
            System.out.println("before auth");


            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmailId(), user.getPassword()));
            System.out.println("after auth");

        } catch (AuthenticationException e) {
            System.out.println("catch");

            //throw new Exception("Incorect username and password ",e);
            return new ResponseEntity("password mismatch", HttpStatus.BAD_REQUEST);
        }

        System.out.println("outside catch");
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmailId());
        //Response tempUser= userService.getUser(user.getEmailId());
        //User temp= (User) tempUser.getResponseObject();
        User temp= getUser(user.getEmailId());
        System.out.println(temp.getFirstName());
        final String jwt = jwtUtil.generateToken(userDetails);
        return new ResponseEntity(jwt, HttpStatus.ACCEPTED);
    }

    public User getUser(String emailId){
        User user=userRepository.findByEmailId("bhagya@gmail.com");
       //return new Response("user deatails fetch",user);
        return user;
    }


}
