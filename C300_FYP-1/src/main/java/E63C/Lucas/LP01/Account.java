/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Dec-24 3:40:42 pm 
 * 
 */

/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Dec-24 3:40:42 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.sql.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String accountName;
    private String accountNumber;
    private double balance;
    private Date creationDate;
    private String bankName;
    private String holderName;

    @Enumerated(EnumType.STRING) // Store enum as string in the database
    private AccountStatus status;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private Account_type accountType; // Link to Account_type

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false) // Member associated with account
    private Member member; // This links the account to a specific member

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            this.status = AccountStatus.PENDING; // Default value
        }
        if (creationDate == null) {
            this.creationDate = new Date(System.currentTimeMillis()); // Default to current date
        }
    }

    // Getters and setters
    
    public int getId() {
        return id;
    }

    public String getHolderName() {
		return holderName;
	}

	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Account_type getAccountType() {
        return accountType;
    }

    public void setAccountType(Account_type accountType) {
        this.accountType = accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    // Enum for account status
    public enum AccountStatus {
        PENDING,
        ACTIVE,
        SUSPENDED,
        CANCELLED_PENDING,
        CANCELLED,
        REJECTED
    }
}
