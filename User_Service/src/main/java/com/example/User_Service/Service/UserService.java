package com.example.User_Service.Service;


import com.example.User_Service.Model.User;
import com.example.User_Service.Repository.UserRepository;
import com.example.User_Service.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> signUp(User user) {

        User savedUser= userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(new Response("User inserted to database successfully", savedUser));
    }


}
