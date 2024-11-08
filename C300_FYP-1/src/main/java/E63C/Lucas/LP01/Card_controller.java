/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-06 7:33:28 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lenovo
 *
 */
@Controller
public class Card_controller {

	@Autowired
	private CardRepository cardrepository;

	@Autowired
	private AccountRepository accountrepository;

	@GetMapping("/Card")
	public String ViewCard(Model model) {

		List<Card> listCard = cardrepository.findAll();

		model.addAttribute("listCard", listCard);

		return "ViewCard";
	}

	@GetMapping("/Card/add")
	public String addCard(Model model, @RequestParam(value = "duplicate", required = false) boolean isDuplicate) {
		model.addAttribute("card", new Card());

		// Use accountRepository to retrieve the list of account types
		List<Account_type> accountTypeList = accountrepository.findAll();
		model.addAttribute("accountTypeList", accountTypeList);

		if (isDuplicate) {
			model.addAttribute("duplicateCardError", true);
		}

		return "AddCard";
	}

	@PostMapping("/Card/save")
	public String saveCard(Card card, Model model) {
	    // Check for duplicates
	    List<Card> existingCardnumber = cardrepository.findByCardnumber(card.getCardnumber());
	    
	    if (!existingCardnumber.isEmpty()) {
	        // If a duplicate is found, add the error to the model and return to AddCard view
	        model.addAttribute("duplicateCard_Error", true);

	        // Pass the current card and account type list to the model to keep form data
	        List<Account_type> accountTypeList = accountrepository.findAll();
	        model.addAttribute("accountTypeList", accountTypeList);
	        model.addAttribute("card", card); // Retain the entered card data
	        return "AddCard"; // Return to the AddCard page without redirect
	    }

	    // If no duplicate, save the card and redirect
	    cardrepository.save(card);
	    return "redirect:/Card"; // Successful save, redirect to ViewCard page
	}


}
