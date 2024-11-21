///**
// * 
// * I declare that this code was written by me, Lenovo. 
// * I will not copy or allow others to copy my code. 
// * I understand that copying code is considered as plagiarism.
// * 
// * Student Name: Lucas poh zhan le
// * Student ID: 22017175
// * Class: E63C
// * Date created: 2024-Nov-20 7:43:23 pm 
// * 
// */
//
//package E63C.Lucas.LP01;
//
//import java.time.LocalDateTime;
//
//import jakarta.persistence.*;
//
//@Entity
//public class ConfirmationToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String token;
//
//    @OneToOne
//    private Card card;
//
//    private LocalDateTime createdAt;
//
//    public ConfirmationToken() {
//    }
//
//    public ConfirmationToken(String token, Card card) {
//        this.token = token;
//        this.card = card;
//        this.createdAt = LocalDateTime.now();
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public Card getCard() {
//        return card;
//    }
//
//    public void setCard(Card card) {
//        this.card = card;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//}
