package com.example.User_Service.Service;

import com.example.User_Service.Model.User;
import com.example.User_Service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailsService  implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username+" line 19");
        User user =userRepository.findByEmailId(username);
        return new org.springframework.security.core.userdetails.User(user.getEmailId(),user.getPassword(),new ArrayList<>());
    }
}
