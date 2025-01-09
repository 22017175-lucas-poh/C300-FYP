/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-06 7:34:52 pm 
 * 
 */
package E63C.Lucas.LP01;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Integer> {

    // Find cards by associated member

    // Find cards by card number (assuming card number is a String)
//    List<Card> findByCardNumber(String cardNumber); // Change to String if cardNumber is a String
    List<Card> findByCardNumber(int cardNumber);  // Use this if cardNumber is stored as int

    // Find cards by status
    List<Card> findByStatus(Card.CardStatus status); // Use CardStatus enum directly

    // Find a card by memberId (assuming a one-to-one relationship between member and card)
//    Card findByCardNumber(int cardNumber); 
    List<Card> findByMember(Member member);

	/**
	 * @param cardNumber
	 * @return
	 */
	Card findFirstByCardNumber(int cardNumber);

}
