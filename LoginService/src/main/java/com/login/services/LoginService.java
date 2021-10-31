package com.login.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.entity.Login;
import com.login.repo.LoginRepo;

@Service
public class LoginService {
	@Autowired
	private LoginRepo loginRepo;
	
	public Login saveuser(Login user) {
		return loginRepo.save(user);
	}
	public Login fetchuserId(String useremail) {
		return loginRepo.findByUseremail(useremail);
	}
	public Login fetchuserIdAndPassword(String useremail,Integer password) {
		return loginRepo.findByUseremailAndPassword(useremail, password);
		
	}
//	public Boolean isUserValid(Login cred) {
//		try {
//			Login temp = loginRepo.findById(cred.getUserId()).get();
//			if(temp.getPassword().equals(cred.getPassword()))
//				return true;
//			return false;
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return false;
//		}
	

}
