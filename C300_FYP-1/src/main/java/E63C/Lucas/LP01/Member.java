
package E63C.Lucas.LP01;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * 
 */
@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int accountId;

	private String name;
	private String password;
	private String email;
	private String role;
	private String nric;
	private String bankName;
	private char gender;
	private Date dob;
	private int mobile;
	private String country;

	public int getAccountID() {
		return accountId;
	}

	public void setAccountID(int accountID) {
		accountId = accountID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		role = role;
	}

	public String getNRIC() {
		return nric;
	}

	public void setNRIC(String nric) {
		nric = nric;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		gender = gender;
	}

	public Date getDOB() {
		return dob;
	}

	public void setDOB(Date dob) {
		dob = dob;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		mobile = mobile;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		country = country;
	}
	

}
