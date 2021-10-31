package com.login.dao;

import com.login.entity.Login;

public class LoginDao {
	private String userId;
	private Integer password;
	public String getUserid() {
		return userId;
	}
	public void setUserid(String userid) {
		this.userId = userid;
	}
	public Integer getPassword() {
		return password;
	}
	public void setPassword(Integer password) {
		this.password = password;
	}
	public Login getobject(LoginDao loginDao) {
		Login cred = new Login();
		cred.setUserId(loginDao.getUserid());
		cred.setPassword(loginDao.getPassword());
		return cred;
	}

}
