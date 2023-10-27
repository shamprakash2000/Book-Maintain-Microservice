package com.example.User_Service.Controller;


import com.example.User_Service.Model.User;
import com.example.User_Service.Response.Response;
import com.example.User_Service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("userService/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.status(HttpStatus.OK).body("User Service up and Running");
    }



    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody User user){
        return userService.signUp(user);
    }
}
