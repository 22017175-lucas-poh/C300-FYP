/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-04 5:45:28 pm 
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
public class Account_type_controller {
	
	@Autowired
	private AccountRepository accountrepository;
	
	@GetMapping("/Account_type")
	public String ViewAccount_type(Model model) {
		
		List<Account_type> listAccount_type = accountrepository.findAll();
		
		model.addAttribute("listAccount_type",listAccount_type);
		return "ViewAccount_type.html";
	}
	

}
