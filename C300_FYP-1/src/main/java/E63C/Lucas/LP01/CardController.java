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

import E63C.Lucas.LP01.Card.CardStatus;

@Controller
public class CardController {

	@Autowired
	private CardRepository cardRepository;

//	@Autowired
//	private PendingCardRepository pendingCardRepository;

	@Autowired
	private AccountRepository accountRepository;

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
		model.addAttribute("card", new Card());
		List<Account_type> accountTypeList = accountRepository.findAll();
		model.addAttribute("accountTypeList", accountTypeList);
		return "AddCard"; // A page for adding a new card
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
		List<Card> existingCardNumbers = cardRepository.findByCardNumber(card.getCardNumber());
		if (!existingCardNumbers.isEmpty()) {
			model.addAttribute("duplicateCard_Error", true);
			List<Account_type> accountTypeList = accountRepository.findAll();
			model.addAttribute("accountTypeList", accountTypeList);
			model.addAttribute("card", card);
			return "AddCard"; // Return to add card page if duplicate is found
		}

		// Save the card to the database
		cardRepository.save(card);

		// Send email to admin
		String adminEmail = "lucaspoh7@gmail.com";
		String subject = "Card Application Received";
		String body = "A new card has been applied with the following details:\n" + "Card Number: "
				+ card.getCardNumber() + "\n" + "Account Type: " + card.getAccount_type().getName() + "\n" + "CVV/CVC: "
				+ card.getCVV() + "\n" + "Expiry Date: " + card.getExpiryDate() + "\n" + "Bank Name: "
				+ card.getBankName() + "\n\n" + "Please review this application.";

		sendEmail(adminEmail, subject, body);

		// Redirect to the success page
		return "redirect:/Card";
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
	public String editCard(@PathVariable("id") int id, Model model) {
		// Fetch the card using the Integer type id
		Card card = cardRepository.findById(id).orElse(null);
		if (card == null) {
			return "redirect:/Admin/Card"; // Redirect if card not found
		}

		// Add the card and account types to the model
		List<Account_type> accountTypeList = accountRepository.findAll();
		model.addAttribute("card", card);
		model.addAttribute("accountTypeList", accountTypeList);

		return "EditCard"; // Return the edit card page
	}

	@PostMapping("/Admin/Card/Edit/{id}")
	public String SaveUpdatedCard(@PathVariable("id") Integer cardId, @ModelAttribute Card card, Model model) {
		Card existingCard = cardRepository.findById(cardId).orElse(null);

		if (existingCard == null) {
			model.addAttribute("error", "Card not found.");
			return "redirect:/Admin/Card"; // Redirect if card not found
		}

		existingCard.setAccount_type(card.getAccount_type());
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
			int cardNumberInt = Integer.parseInt(cardNumber);

			// Fetch the list of cards matching the card number
			List<Card> cards = cardRepository.findByCardNumber(cardNumberInt);

			if (cards.isEmpty()) {
				model.addAttribute("error", "Card not found.");
				return "redirect:/Card";
			}

			// Handle multiple matches (e.g., show the first card or prompt user to choose)
			Card card = cards.get(0);

			// Add balance and card details to the model
			model.addAttribute("card", card);
			model.addAttribute("balance", card.getBalance());

			return "ViewBalance"; // Return the balance view page
		} catch (NumberFormatException e) {
			model.addAttribute("error", "Invalid card number.");
			return "redirect:/Card";
		}
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


	public void sendEmail(String to, String subject, String body) {
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(to);
			msg.setSubject(subject);
			msg.setText(body);
			msg.setFrom("musashibestgirl990@gmail");

			javaMailSender.send(msg);
			System.out.println("Email sent successfully to: " + to);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
