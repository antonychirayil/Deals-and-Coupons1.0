package com.login.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.login.entity.Login;

@Repository
public interface LoginRepo extends MongoRepository<Login, String>{

	public Login findByUseremail(String useremail);

	public Login findByUseremailAndPassword(String useremail, Integer password);

}
