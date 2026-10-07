package com.login.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cred")
public class Login {
	@Id
	private String userId;
	private String useremail;
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	private Integer password;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getPassword() {
		return password;
	}
	public void setPassword(Integer password) {
		this.password = password;
	}
	public Login(String userId, Integer password,String useremail) {
		super();
		this.userId = userId;
		this.password = password;
		this.useremail=useremail;
	}
	public Login() {
		super();
	}
	@Override
	public String toString() {
		return "Login [userId=" + userId + ", useremail=" + useremail + ", password=" + password + "]";
	}
	
	


	}
	
	


