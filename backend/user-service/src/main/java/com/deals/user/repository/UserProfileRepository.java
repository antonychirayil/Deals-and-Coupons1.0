package com.deals.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.deals.user.entity.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
}
