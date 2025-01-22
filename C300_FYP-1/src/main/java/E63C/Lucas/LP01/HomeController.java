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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Lenovo
 *
 */
@Controller
public class HomeController {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
		@GetMapping("/")
		public String home() {
			return"index";
		}
		@GetMapping("/403")
		public String error403() {
			return"403";
		}
		@GetMapping("/calculator")
		public String calculator() {
			return"calculator";
		}
		@GetMapping("/contact")
		public String contact() {
			return"contact";
		}
		
		@PostMapping("/submitContact")
		public String submitContact(@RequestParam String name, @RequestParam String email,
		                             @RequestParam String subject, @RequestParam String message,
		                             RedirectAttributes redirectAttributes) {
		    try {
		        sendFeedbackEmail(email, name, subject, message);
		        redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");
		    } catch (Exception e) {
		        redirectAttributes.addFlashAttribute("error", "An error occurred while sending your message. Please try again.");
		        e.printStackTrace();
		    }

		    return "redirect:/contact"; // Ensure '/contact' is correctly mapped
		}

		
		// Feedback email logic
		public void sendFeedbackEmail(String senderEmail, String name, String subject, String message) {
		    String emailBody = "Feedback from: " + name + "\nEmail: " + senderEmail +
		                       "\n\nSubject: " + subject + "\n\nMessage:\n" + message;

		    sendEmail("lutfilh2702@gmail.com", "New Feedback from RP Digital Bank", emailBody);
		}
		
		public void sendEmail(String to, String subject, String body) {
		    try {
		        SimpleMailMessage msg = new SimpleMailMessage();
		        msg.setTo(to);
		        msg.setSubject(subject);
		        msg.setText(body);
		        msg.setFrom("musashibestgirl990@gmail.com"); // Ensure 'From' email matches SMTP settings

		        javaMailSender.send(msg);
		        System.out.println("Email sent successfully to: " + to);
		    } catch (Exception e) {
		        System.err.println("Failed to send email to: " + to);
		        e.printStackTrace();
		    }
		}
}
