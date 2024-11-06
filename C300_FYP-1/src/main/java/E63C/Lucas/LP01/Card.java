/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-06 6:50:09 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * @author Lenovo
 *
 */
@Entity
public class Card {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
 private int Cardnumber;
 private String CardName;
 private int CVV;
 private Date ExpiryDate;
 private String bankName;
 
 @ManyToOne
 @JoinColumn(name="account_type name",nullable=false)
 
 private Account_type account_type;
  
public Account_type getAccount_type() {
	return account_type;
}
public void setAccount_type(Account_type account_type) {
	this.account_type = account_type;
}

public int getCardnumber() {
	return Cardnumber;
}
public void setCardnumber(int cardnumber) {
	Cardnumber = cardnumber;
}

public String getCardName() {
	return CardName;
}
public void setCardName(String cardName) {
	CardName = cardName;
}
public int getCVV() {
	return CVV;
}
public void setCVV(int cVV) {
	CVV = cVV;
}
public Date getExpiryDate() {
	return ExpiryDate;
}
public void setExpiryDate(Date expiryDate) {
	ExpiryDate = expiryDate;
}
public String getBankName() {
	return bankName;
}
public void setBankName(String bankName) {
	this.bankName = bankName;
}
 
 
 
}
