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
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int cardNumber;

    private String cardName;
    private int CVV;
    private Date expiryDate;
    private String bankName;
    private int balance;

    @Enumerated(EnumType.STRING) // Store enum as string in the database
    private CardStatus status;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private Account_type account_type;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false) // Member associated with card
    private Member member; // This links the card to a specific member

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            this.status = CardStatus.PENDING; // Default value
        }
    }

    // Getters and setters remain as they were
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getCVV() {
        return CVV;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Account_type getAccount_type() {
        return account_type;
    }

    public void setAccount_type(Account_type account_type) {
        this.account_type = account_type;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    // Enum for card status
    public enum CardStatus {
        PENDING,
        APPROVED,
        REJECTED;
    }
}
