package com.nagarro.ordermanagement.dto;

public class AppUserDto {

	private String userName;

	private String fullName;

	private String email;

	private String password;

	private String userType;

	private String serviceType;

	private Long pinCode;
	
	public AppUserDto() {}

	public AppUserDto(String userName, String fullName, String email, String password, String userType,
			String serviceType, Long pinCode) {
		this.userName = userName;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.serviceType = serviceType;
		this.pinCode = pinCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Long getPinCode() {
		return pinCode;
	}

	public void setPinCode(Long pinCode) {
		this.pinCode = pinCode;
	}

	@Override
	public String toString() {
		return "AppUserDto [userName=" + userName + ", fullName=" + fullName + ", email=" + email + ", password="
				+ password + ", userType=" + userType + ", serviceType=" + serviceType + ", pinCode=" + pinCode + "]";
	}
	
	

}
