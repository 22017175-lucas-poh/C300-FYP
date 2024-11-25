package E63C.Lucas.LP01;

import java.util.List;

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

import E63C.Lucas.LP01.Card.CardStatus;

@Controller
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PendingCardRepository pendingCardRepository;

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
        return "ViewCard";  // A page that shows cards
    }

    @GetMapping("/Card/add")
    public String addCard(Model model) {
        model.addAttribute("card", new Card());
        List<Account_type> accountTypeList = accountRepository.findAll();
        model.addAttribute("accountTypeList", accountTypeList);
        return "AddCard";  // A page for adding a new card
    }

    @PostMapping("/Card/save")
    public String saveCard(@ModelAttribute Card card, Model model) {
        // Get the current logged-in member
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Assuming the user is identified by 'username'

        // Fetch the member based on the username
        Member loggedInMember = memberRepository.findByUsername(username);

        // Set the member to the card
        card.setMember(loggedInMember);

        // Check for duplicate card number
        List<Card> existingCardnumber = cardRepository.findByCardNumber(card.getCardNumber());

        if (!existingCardnumber.isEmpty()) {
            model.addAttribute("duplicateCard_Error", true);
            List<Account_type> accountTypeList = accountRepository.findAll();
            model.addAttribute("accountTypeList", accountTypeList);
            model.addAttribute("card", card);
            return "AddCard";  // Return to add card page if there is a duplicate
        }

        // Save the card temporarily
        pendingCardRepository.save(card);
        System.out.println("Card saved temporarily: " + card.getCardNumber());

        // Send email to admin (as before)
        String adminEmail = "lucaspoh7@gmail.com";  // Replace with your admin email
        String subject = "Card Application Received";
        String body = "A new card has been applied with the following details:\n" +
                      "Card Number: " + card.getCardNumber() + "\n" +
                      "Account Type: " + card.getAccount_type().getName() + "\n" +
                      "CVV/CVC: " + card.getCVV() + "\n" +
                      "Expiry Date: " + card.getExpiryDate() + "\n" +
                      "Bank Name: " + card.getBankName() + "\n\n" +
                      "Please review this application.";

        sendEmail(adminEmail, subject, body);

        return "redirect:/Card";
    }
    @GetMapping("/Admin/Card")
    public String viewAllCards(Model model) {
        // Fetch all cards in the system (admin can view all cards)
        List<Card> allCards = cardRepository.findAll();

        // Add the list of cards to the model to be rendered in the admin's view
        model.addAttribute("listCard", allCards);

        return "AdminViewCards";  // Return the page for viewing all cards by the admin
    }

    @PostMapping("/Admin/Card/Approve/{id}")
    public String approveCard(@PathVariable("id") Integer cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card != null && card.getStatus() == CardStatus.PENDING) {
            card.setStatus(CardStatus.APPROVED); // Update status to APPROVED
            cardRepository.save(card); // Save the updated card
        }
        return "redirect:/Admin/Cards"; // Redirect to view the updated list
    }

    @PostMapping("/Admin/Card/Reject/{id}")
    public String rejectCard(@PathVariable("id") Integer cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card != null && card.getStatus() == CardStatus.PENDING) {
            card.setStatus(CardStatus.REJECTED); // Update status to REJECTED
            cardRepository.save(card); // Save the updated card
        }
        return "redirect:/Admin/Cards"; // Redirect to view the updated list
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
