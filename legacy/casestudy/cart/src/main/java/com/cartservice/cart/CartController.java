package com.cartservice.cart;

import org.springframework.beans.factory.annotation.Autowired;

public class CartController {

    @Autowired
    private CartRepository cartRepository;

    private UserRepository userRepository;



}
