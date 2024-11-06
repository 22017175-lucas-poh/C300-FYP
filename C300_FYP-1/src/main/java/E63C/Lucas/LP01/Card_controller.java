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
			
			model.addAttribute("listCard",listCard);
			
			return "ViewCard";
		}
		
		
		
	
	
}
