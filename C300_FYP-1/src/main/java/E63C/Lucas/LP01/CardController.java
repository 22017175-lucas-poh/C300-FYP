package E63C.Lucas.LP01;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import E63C.Lucas.LP01.Card.CardStatus;

@Controller
public class CardController {

	@Autowired
	private CardRepository cardRepository;

//	@Autowired
//	private PendingCardRepository pendingCardRepository;
//	@Autowired
//	private MemberService memberService;
	@Autowired
    private cardService cardService; // Inject CardService here
	@Autowired
	private AccountTypeRepository accountRepository;
	@Autowired
	private CardTypeRepository cardTypeRepository;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private MemberRepository memberRepository;

	// This method fetches only the cards of the currently logged-in member
	@GetMapping("/Card")
	public String viewCard(Model model) {
		// Get the current logged-in user from the authentication context
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Assuming the user is identified by 'username'

		// Fetch the member based on the username (from the Member repository)
		Member loggedInMember = memberRepository.findByUsername(username);

		// Fetch the cards associated with the logged-in member
		List<Card> userCards = cardRepository.findByMember(loggedInMember);

		model.addAttribute("listCard", userCards);
		return "ViewCard"; // A page that shows cards
	}

	@GetMapping("/Card/add")
	public String addCard(Model model) {
		model.addAttribute("card", new Card()); // Empty Card object for form binding
		List<Card_type> cardTypeList = cardTypeRepository.findAll(); // Get list of CardType from DB
		model.addAttribute("cardTypeList", cardTypeList); // Add CardType list to the model
		return "AddCard"; // Page for adding a new card
	}

	@PostMapping("/Card/save")
	public String saveCard(@ModelAttribute Card card, Model model) {
	    // Constants for Card Number and CVV Generation
	    final int CARD_NUMBER_MIN = 100_000_000;
	    final int CARD_NUMBER_MAX = 999_999_999;
	    final int CVV_MIN = 100;
	    final int CVV_MAX = 999;

	    Random random = new Random();

	    // Get the current logged-in member
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();

	    // Fetch the member based on the username
	    Member loggedInMember = memberRepository.findByUsername(username);

	    // Set the member to the card
	    card.setMember(loggedInMember);

	    // Generate a random card number if not provided
	    if (card.getCardNumber() == 0) {
	        card.setCardNumber(CARD_NUMBER_MIN + random.nextInt(CARD_NUMBER_MAX - CARD_NUMBER_MIN + 1));
	    }

	    // Generate a random CVV if not provided
	    if (card.getCVV() == 0) {
	        card.setCVV(CVV_MIN + random.nextInt(CVV_MAX - CVV_MIN + 1));
	    }

	    // Set expiry date to 4 years from now if not provided
	    if (card.getExpiryDate() == null) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.add(Calendar.YEAR, 4); // Add 4 years to the current date
	        java.util.Date utilDate = calendar.getTime(); // Get java.util.Date
	        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); // Convert to java.sql.Date
	        card.setExpiryDate(sqlDate); // Set the expiry date
	    }

	    // Set bank name to "RP Digital Bank" if not provided
	    if (card.getBankName() == null || card.getBankName().isEmpty()) {
	        card.setBankName("RP Digital Bank");
	    }

	    // Check for duplicate card number
	    // We are now checking if the card number already exists in the database
	    Card existingCard = cardRepository.findFirstByCardNumber(card.getCardNumber());
	    if (existingCard != null) {
	        model.addAttribute("duplicateCard_Error", true);

	        // Get list of card types for display in case of error
	        List<Card_type> cardTypeList = cardTypeRepository.findAll();
	        model.addAttribute("cardTypeList", cardTypeList);

	        model.addAttribute("card", card); // Passing the card with all its data
	        return "AddCard"; // Return to add card page if duplicate is found
	    }

	    // Save the card to the database
	    cardRepository.save(card);

	    // Send email to admin
	    String adminEmail = "lucaspoh7@gmail.com";
	    String subject = "Card Application Received";
	    String body = "A new card has been applied with the following details:\n" + "Card Number: "
	            + card.getCardNumber() + "\n" + "Card Type: " + card.getCardType().getName() + "\n" + // Changed AccountType to CardType
	            "CVV/CVC: " + card.getCVV() + "\n" + "Expiry Date: " + card.getExpiryDate() + "\n" + "Bank Name: "
	            + card.getBankName() + "\n\n" + "Please review this application.";

	    sendEmail(adminEmail, subject, body);

	    // Redirect to the success page
	    return "redirect:/Card"; // Redirect to the list or another page
	}

	@GetMapping("/Admin/Card")
	public String viewAllCards(Model model) {
		// Fetch all cards in the system (admin can view all cards)
		List<Card> allCards = cardRepository.findAll();

		// Add the list of cards to the model to be rendered in the admin's view

		model.addAttribute("listCard", allCards);

		return "AdminViewCards"; // Return the page for viewing all cards by the admin
	}

	@PostMapping("/Admin/Card/Approve/{id}")
	public String approveCard(@PathVariable("id") Integer cardId) {
		Card card = cardRepository.findById(cardId).orElse(null);
		if (card != null && card.getStatus() == CardStatus.PENDING) {
			card.setStatus(CardStatus.APPROVED); // Update status to APPROVED
			cardRepository.save(card); // Save the updated card
		}
		return "redirect:/Admin/Card"; // Redirect to view the updated list
	}

	@PostMapping("/Admin/Card/Reject/{id}")
	public String rejectCard(@PathVariable("id") Integer cardId) {
		Card card = cardRepository.findById(cardId).orElse(null);
		if (card != null && card.getStatus() == CardStatus.PENDING) {
			card.setStatus(CardStatus.REJECTED); // Update status to REJECTED
			cardRepository.save(card); // Save the updated card
		}
		return "redirect:/Admin/Card"; // Redirect to view the updated list
	}

	@GetMapping("/Admin/Card/Edit/{id}")
	public String editCard(@PathVariable("id") Integer id, Model model) {
		// Fetch the card by its ID
		Card card = cardRepository.findById(id).orElse(null);
		if (card == null)
			return "redirect:/Admin/Card"; // If card is not found, redirect to the card list.

		// Add the card object to the model
		model.addAttribute("card", card);

		// Fetch all card types and pass them to the view
		List<Card_type> cardTypeList = cardTypeRepository.findAll();
		model.addAttribute("cardTypeList", cardTypeList); // Correct attribute name to match your form.

		// Return the "EditCard" view for editing the selected card
		return "EditCard";
	}

	@PostMapping("/Admin/Card/Edit/{id}")
	public String SaveUpdatedCard(@PathVariable("id") Integer cardId, @ModelAttribute Card card, Model model) {
		Card existingCard = cardRepository.findById(cardId).orElse(null);

		if (existingCard == null) {
			model.addAttribute("error", "Card not found.");
			return "redirect:/Admin/Card"; // Redirect if card not found
		}

		existingCard.setCardType(card.getCardType());
		existingCard.setCardNumber(card.getCardNumber());
		existingCard.setCVV(card.getCVV());
		existingCard.setExpiryDate(card.getExpiryDate());
		existingCard.setCardName(card.getCardName());
		existingCard.setBankName(card.getBankName());

		cardRepository.save(existingCard);

		return "redirect:/Admin/Card";
	}

	@PostMapping("/Admin/Card/Delete/{id}")
	public String deleteCard(@PathVariable("id") Integer id) {
		cardRepository.deleteById(id);

		return "redirect:/Admin/Card";
	}
	@GetMapping("/Card/Balance/View/{id}")
	public String viewBalance(@PathVariable("id") String cardNumber, Model model) {
	    try {
	        int cardNumberInt = Integer.parseInt(cardNumber); // Parse card number from the path variable

	        // Fetch the list of cards matching the card number
	        List<Card> cards = cardRepository.findByCardNumber(cardNumberInt); // Returns List<Card>

	        if (cards.isEmpty()) {
	            model.addAttribute("error", "Card not found.");
	            return "redirect:/Card";
	        }

	        // Now, extract the first card from the list if it exists
	        Card card = cards.get(0); // Correctly fetching a single Card from the list

	        // Add the card and balance to the model
	        model.addAttribute("card", card); // Passing the card details
	        model.addAttribute("balance", card.getBalance()); // Adding the card balance

	        return "ViewBalance"; // Returning the balance view page
	    } catch (NumberFormatException e) {
	        model.addAttribute("error", "Invalid card number.");
	        return "redirect:/Card"; // Redirecting back if the card number is invalid
	    }
	}
//	 @PostMapping("/updateBalance")
//	    public ResponseEntity<Map<String, Object>> updateBalance(@RequestBody Map<String, Object> requestData) {
//	        String cardNumber = (String) requestData.get("cardNumber");
//	        String transactionId = (String) requestData.get("transactionId");
//	        Double amountToAdjust = (Double) requestData.get("amountToAdjust");
//
//	        boolean success = cardService.updateBalance(cardNumber, amountToAdjust); // Method to update balance in your service
//
//	        Map<String, Object> response = new HashMap<>();
//	        response.put("success", success);
//	        return ResponseEntity.ok(response);
//	    }
	@PostMapping("/CardController/updateBalance")
	public RedirectView updateBalance(@RequestParam int cardNumber,
	                                   @RequestParam Double amount,
	                                   @RequestParam String transactionId) {
	    // Find the card by card number
	    Card card = cardRepository.findFirstByCardNumber(cardNumber);

	    // If card is not found, handle error (optional)
	    if (card == null) {
	        return new RedirectView("/errorPage");  // You can define an error page
	    }

	    // Update balance
	    Double updatedBalance = card.getBalance() + amount;
	    card.setBalance(updatedBalance);

	    // Save the updated card back to the database
	    cardRepository.save(card);

	    // Log transaction details (for debugging purposes)
	    System.out.println("Card Number: " + cardNumber);
	    System.out.println("Amount: " + amount);
	    System.out.println("Transaction ID: " + transactionId);
	    System.out.println("Updated Balance: " + updatedBalance);

	    // Redirect back to the card page after successful update
	    return new RedirectView("/Card"); // might change the redirection link to a Success.html page if have time to create.
	}





	@PostMapping("/Card/cancel/{id}")
	public String cancelCard(@PathVariable("id") Integer cardId) {
		// Fetch the card by its ID
		Card card = cardRepository.findById(cardId).orElse(null);

		// Check if the card exists and update the status to CANCELLED_PENDING
		if (card != null) {
			card.setStatus(Card.CardStatus.CANCELLED_PENDING); // Set the status to CANCELLED_PENDING
			cardRepository.save(card); // Save the updated card
		}

		// Redirect to the card list or the appropriate page
		return "redirect:/Card";
	}

	@PostMapping("/Admin/Card/Cancel/Approve/{id}")
	public String approveCancellation(@PathVariable("id") Integer cardId) {
		Card card = cardRepository.findById(cardId).orElse(null);
		if (card != null && card.getStatus() == CardStatus.CANCELLED_PENDING) {
			card.setStatus(CardStatus.CANCELLED); // Update status to CANCELLED
			cardRepository.save(card); // Save the updated card
		}
		return "redirect:/Admin/Card"; // Redirect to view the updated list
	}

	@PostMapping("/Admin/Card/Cancel/Reject/{id}")
	public String rejectCancellation(@PathVariable("id") Integer cardId) {
		Card card = cardRepository.findById(cardId).orElse(null);
		if (card != null && card.getStatus() == CardStatus.CANCELLED_PENDING) {
			card.setStatus(CardStatus.APPROVED); // Update status to APPROVED again
			cardRepository.save(card); // Save the updated card
		}
		return "redirect:/Admin/Card"; // Redirect to view the updated list
	}

	/**
     * Update the balance of a card using its card number.
     *
     * @param request Contains card number, transaction ID, and the amount to update.
     * @return Response indicating success or failure.
     */
//	 @GetMapping("/updateBalance")
//	    public String updateBalance(@RequestParam int cardNumber, @RequestParam double newBalance, Model model) {
//	        // Update card balance
//	        cardService.updateCardBalance(cardNumber, newBalance);
//	        
//	        // Fetch updated card info and display it
//	        Card updatedCard = cardService.findCardByNumber(cardNumber);
//	        model.addAttribute("card", updatedCard);
//	        
//	        return "cardDetails"; // The view name to display the updated details
//	    }
	

	public void sendEmail(String to, String subject, String body) {
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(to);
			msg.setSubject(subject);
			msg.setText(body);
			msg.setFrom("musashibestgirl990@gmail.com");

			javaMailSender.send(msg);
			System.out.println("Email sent successfully to: " + to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
