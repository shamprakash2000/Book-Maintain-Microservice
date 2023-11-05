package com.example.User_Service.Configuration;

import com.example.User_Service.Model.User;
import com.example.User_Service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;


@Configuration
public class MongoConfiguration  implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    public MongoConfiguration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void createUniqueIndex() {


        IndexOperations indexOps = mongoTemplate.indexOps(User.class);
        Index emailIndex = new Index().on("emailId", Sort.Direction.ASC).unique();
        Index phoneIndex = new Index().on("phoneNumber", Sort.Direction.ASC).unique();
        indexOps.ensureIndex(emailIndex);
        indexOps.ensureIndex(phoneIndex);

    }

    @Override
    public void run(String... args) throws Exception {
        User savedUser=userRepository.findByEmailId("admin@gmail.com");
        if(savedUser==null){
            User user =new User();
            user.setRole("ADMIN");
            String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
            user.setPassword(hashedPassword);
            user.setIsUserDeleted(false);
            user.setPhoneNumber("9999999999");
            user.setFirstName("Admin First Name");
            user.setLastName("Admin Last Name");
            user.setEmailId("admin@gmail.com");
            user.setUserSignUpDate(LocalDateTime.now());
            user.setLastModifiedDate(LocalDateTime.now());
            userRepository.save(user);
        }


    }
}