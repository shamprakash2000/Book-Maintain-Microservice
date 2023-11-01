package com.example.User_Service.Repository;

import com.example.User_Service.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmailId(String emailId);
    User findByphoneNumber(String number);

    Boolean existsByEmailId(String emailId);
    Boolean existsByPhoneNumber(String phoneNumber);
}
