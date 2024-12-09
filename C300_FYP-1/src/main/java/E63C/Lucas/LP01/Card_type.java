/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-01 5:50:52 pm 
 * 
 */

package E63C.Lucas.LP01;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Card_type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name; // Card type name (e.g., CREDIT, DEBIT)

    @Column(length = 255)
    private String description; // Additional details about the card type

    @OneToMany(mappedBy = "cardType", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards; // Cards associated with this card type

    // Default Constructor
    public Card_type() {}

    // Parameterized Constructor
    public Card_type(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }
}
