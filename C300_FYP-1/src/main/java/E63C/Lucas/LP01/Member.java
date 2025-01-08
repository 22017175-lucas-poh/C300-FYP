/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Jan-05 10:03:23 am 
 * 
 */

package E63C.Lucas.LP01;

import java.sql.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "custom_id", nullable = true)
	private String customId;
	
	@NotEmpty(message = "Member name cannot be empty")
	@Size(min = 4, max = 50, message = "Member name length must be between 5 and 50 characters!")
	private String name;

	@NotEmpty(message = "Member username cannot be empty")
	@Size(min = 4, max = 75, message = "Member username length must be between 5 and 75 characters!")
	private String username;

	@NotEmpty(message = "Member password cannot be empty")
	@Size(min = 5, max = 75, message = "Member password length must be between 5 and 75 characters!")
	private String password;

	@NotEmpty(message = "Member email cannot be empty")
	@Size(min = 5, max = 50, message = "Member email length must be between 5 and 50 characters!")
	private String email;

	@NotEmpty(message = "Member nric cannot be empty")
	@Size(min = 9, max = 9, message = "Member nric length must be between 9 characters!")
	private String nric;
	
	private String role;
	private boolean accountNonLocked;
	private int failedAttempt;
	private Date lockTime;

	@OneToMany(mappedBy = "member")
	private Set<Consultation> consultations;

	public String getCustomId() {
	    return customId;
	}

	public void setCustomId(String customId) {
	    this.customId = customId;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public int getFailedAttempt() {
		return failedAttempt;
	}

	public void setFailedAttempt(int failedAttempt) {
		this.failedAttempt = failedAttempt;
	}

	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	public String getNric() {
		return nric;
	}

	public void setNric(String nric) {
		this.nric = nric;
	}
}
