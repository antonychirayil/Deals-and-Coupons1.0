package com.userservice.user;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository  extends MongoRepository<User, String> {
    User findUsersById(String Id);

    User findByMobileNumber(String mobilenumber);

    User findByUserName(String username);
}
