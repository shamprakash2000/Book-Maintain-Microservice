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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Lazy
public class UserService {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    public ResponseEntity<?> signUp(User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("User object is empty", null));
        }
        if (user.getEmailId().isBlank() || user.getPassword().isBlank() || user.getPhoneNumber().isBlank() || user.getFirstName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Some of the user field is empty", null));
        }
        String emailId = user.getEmailId().trim().toLowerCase();
        String firstName = user.getFirstName().trim();
        String lastName = user.getLastName().trim();
        String phoneNumber = user.getPhoneNumber().trim();
        user.setEmailId(emailId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setIsUserDeleted(false);
        if (checkEmailIdExists(emailId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Email Id already exists", null));
        }
        if (checkPhoneNumberExists(phoneNumber)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Phone number already exists", null));

        }

        user.setLastModifiedDate(LocalDateTime.now());
        user.setUserSignUpDate(LocalDateTime.now());
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setRole("USER");
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response("Unable to insert data to database", null));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response("User details inserted to database successfully", null));


    }

    public boolean checkEmailIdExists(String emailId) {
        return userRepository.existsByEmailId(emailId);
    }

    public boolean checkPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public ResponseEntity<?> login(User user) {
        String emailId = user.getEmailId().trim().toLowerCase();
        if (!userRepository.existsByEmailId(emailId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Email Id does not exists", null));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmailId(), user.getPassword()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("User not found / Check your password once again", null));
        }

        User savedUser = getUser(user.getEmailId());
        final String jwt = jwtUtil.generateToken(savedUser.getEmailId(), savedUser.getRole());
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("JWT", jwt);
        Response response = new Response("User logged in successfully", hashMap);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public User getUser(String emailId) {

        try {
            User user = userRepository.findByEmailId(emailId);
            if(!user.isUserDeleted()){
                return user;
            }
            return null;

        } catch (Exception e) {
            return null;
            // throw new RuntimeException("Unable to fetch details");

        }

    }

    public String extractJWTFromHTTPHeader(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        String jwt = bearer.substring(7);
        return jwt;
    }

    public String extractEmailIdFromHTTPHeader(HttpServletRequest request) {
        String jwt = extractJWTFromHTTPHeader(request);
        String emailId = jwtUtil.extractAllClaims(jwt).get("emailId", String.class);
        return emailId;
    }


    public ResponseEntity<?> fetchUserDetails(HttpServletRequest request) {
        String emailId = extractEmailIdFromHTTPHeader(request);
        User user = getUser(emailId);
        user.setPassword("");
        return ResponseEntity.status(HttpStatus.OK).body(new Response("User details fetched successfully", user));
    }

    public ResponseEntity<?> updatePhoneNumber(User user, HttpServletRequest request) {

        String emailId = extractEmailIdFromHTTPHeader(request);
        User tempUser = userRepository.findByEmailId(emailId);
        if (!checkEmailIdExists(tempUser.getEmailId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("The user you want to update doesnot exist in the database", null));
        }
        if (checkPhoneNumberExists(user.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Phone number already exists", null));
        } else {
            String phoneNumber = user.getPhoneNumber().trim();
            tempUser.setPhoneNumber(phoneNumber);
            tempUser.setLastModifiedDate(LocalDateTime.now());
            try {
                userRepository.save(tempUser);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response("Unable to insert data to database", null));
            }
            User updatedUser = getUser(tempUser.getEmailId());
            updatedUser.setPassword("");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Phone number updated successfully.", updatedUser));
        }

    }

    public ResponseEntity logout(HttpServletRequest request) {
        String jwt = extractJWTFromHTTPHeader(request);
        tokenBlacklistService.blacklistToken(jwt);
        return ResponseEntity.status(HttpStatus.OK).body(new Response("User logged out successfully", null));
    }

    public ResponseEntity deleteUser(User user, HttpServletRequest request) {
        String jwt = extractJWTFromHTTPHeader(request);
        String role = jwtUtil.extractAllClaims(jwt).get("role", String.class);
        if (role.equals("ADMIN")) {
            User savedUser = getUser(user.getEmailId());
            if (savedUser != null) {
                savedUser.setIsUserDeleted(true);
                userRepository.save(savedUser);
                return ResponseEntity.status(HttpStatus.OK).body(new Response("User deleted successfully.", null));
            }
            else{
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Unable to find given user for delete.", null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("User not allowed to delete, Only admins are allowed to perform this operation.", null));
        }

    }
}
