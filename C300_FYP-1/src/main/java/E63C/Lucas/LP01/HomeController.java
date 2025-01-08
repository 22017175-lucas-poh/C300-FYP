/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-18 3:33:31 pm 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lenovo
 *
 */
@Controller
public class HomeController {
		@GetMapping("/")
		public String home() {
			return"index";
		}
		@GetMapping("/403")
		public String error403() {
			return"403";
		}
		@GetMapping("/contact")
		public String contact() {
			return"contact";
		}
}
