package com.deals.user.service;

import org.springframework.stereotype.Service;

import com.deals.user.dto.CurrentUser;
import com.deals.user.dto.ProfileRequest;
import com.deals.user.dto.ProfileResponse;
import com.deals.user.entity.UserProfile;
import com.deals.user.repository.UserProfileRepository;

@Service
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public ProfileResponse getProfile(CurrentUser user) {
        // A user who never filled in a profile gets an empty one instead of a 404
        UserProfile profile = userProfileRepository.findById(user.id())
                .orElse(new UserProfile(user.id(), null, null));
        return toResponse(user, profile);
    }

    public ProfileResponse updateProfile(CurrentUser user, ProfileRequest request) {
        // save() with an existing id REPLACES the document; with a new id it INSERTS one ("upsert")
        UserProfile saved = userProfileRepository.save(
                new UserProfile(user.id(), request.phone(), request.city()));
        return toResponse(user, saved);
    }

    private ProfileResponse toResponse(CurrentUser user, UserProfile profile) {
        return new ProfileResponse(user.id(), user.name(), user.email(), profile.getPhone(), profile.getCity());
    }
}
