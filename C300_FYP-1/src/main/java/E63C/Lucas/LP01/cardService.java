/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2025-Jan-09 2:41:14 am 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.stereotype.Service;


import org.springframework.stereotype.Service;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class cardService {

    // Logger to log activities for debugging
    private static final Logger logger = LoggerFactory.getLogger(cardService.class);

    private final CardRepository cardRepository;

    // Constructor-based dependency injection for CardRepository
    @Autowired
    public cardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Update the balance of a card.
     *
     * @param cardNumber the card number of the card whose balance needs to be updated.
     * @param newBalance the new balance to be set for the card.
     */
    @Transactional
    public void updateCardBalance(int cardNumber, double newBalance) {
        // Fetch the card using the updated repository method
        Card card = cardRepository.findFirstByCardNumber(cardNumber);

        if (card != null) {
            // Log the current balance before update
            logger.info("Updating card balance. Old balance: " + card.getBalance() + ", New balance: " + newBalance);
            
            // Set the new balance
            card.setBalance(newBalance);
            
            // Save the updated card back to the database
            cardRepository.save(card);
            
            // Log successful update
            logger.info("Card balance updated successfully.");
        } else {
            // Log error if card is not found
            logger.error("Card not found with card number: " + cardNumber);
        }
    }

    public Card findCardByNumber(int cardNumber) {
        return cardRepository.findFirstByCardNumber(cardNumber);  // This will now return a Card
    }

    public double getCardBalance(int cardNumber) {
        Card card = cardRepository.findFirstByCardNumber(cardNumber);  // This will return a single card
        if (card != null) {
            return card.getBalance();
        } else {
            // Handle the case where card is not found
            logger.error("Card not found with card number: " + cardNumber);
            return 0.0;
        }
    }
    
}
