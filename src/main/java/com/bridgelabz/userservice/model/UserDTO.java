/********************************************************************************* *
 * Purpose: To do Login Registration with the help of MONGODB repository thats why creating a POJO class user with suitable fields
	and implementing microservices.
 * 
 * @author Saurav Manchanda
 * @version 1.0
 * @since 11/07/2018
 *********************************************************************************/
package com.bridgelabz.userservice.model;

import java.io.Serializable;
/**
 * @author Saurav
 * <p>
 * This is UserDTO class which follows  
 * </p>
 */
public class UserDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String userName;
	private String email;
	private String password;
	/**
	 * Method for getting the userName
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * Method for setting the userName
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * Method for getting an email
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * Method for setting an email
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * Method for getting the Password
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * Method for setting the password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
