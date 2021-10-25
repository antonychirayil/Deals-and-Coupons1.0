package com.userservice.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    //getusers
    @GetMapping(value = "/userlist")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    //addUsers
    @PostMapping(value = "/addUser")
    public String addUser(@RequestBody User user){
        userRepository.save(user);
        return "user Added Succesfully";
    }
    //deleteUsers
    @DeleteMapping(value = "/delete/{userID}")
    public String deleteUser(@PathVariable String userID) {
        System.out.println("Delete method called");
        userRepository.deleteById(userID);
        return "Deleted Successfully";
    }
    //updateUser
    @PutMapping(value = "/update/{userID}")
    public String updateUser(@RequestBody User user, @PathVariable String userID){
        userRepository.save(user);
        return "user is Updated Succesfully";
    }
//    //--------------user-getCoupons---------------------------------------------------------------
//    @RequestMapping(value = "/getCoupons")
//    public String getAllCoupons(){
//        return restTemplate.getForObject("http://coupons/coupons/list",String.class);
//    }

}
