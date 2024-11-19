/**
 * 
 * I declare that this code was written by me, 22024937. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Royce
 * Student ID: 22024937
 * Class: E63C
 * Date created: 2024-Nov-15 1:33:28 pm 
 * 
 */

package E63C.Lucas.LP01;

/**
 * @author 22024937
 *
 */
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.*;

	import java.time.LocalDate;
	import java.time.LocalTime;

	@Controller
	@RequestMapping("/consultations")
	public class ConsultationController {

	    @Autowired
	    private ConsultationRepository consultationRepository;

	    // GET request to show the booking form
	    @GetMapping("/book")
	    public String showBookingForm(Model model) {
	        model.addAttribute("consultation", new Consultation());
	        return "consultationForm"; // Renders the booking form
	    }

	    // POST request to handle booking submission
	    @PostMapping("/book")
	    public String bookConsultation(@RequestParam String userName,
	                                   @RequestParam int id,
	                                   @RequestParam String date,
	                                   @RequestParam String time,
	                                   @RequestParam String consultantName,
	                                   Model model) {
	        // Parse date and time
	        LocalDate appointmentDate = LocalDate.parse(date);
	        LocalTime appointmentTime = LocalTime.parse(time);

	        // Create and save new consultation
	        Consultation consultation = new Consultation(userName, id, appointmentDate, appointmentTime, consultantName);
	        consultationRepository.save(consultation);

	        // Pass consultation details to the model for the confirmation page
	        model.addAttribute("consultation", consultation);
	        return "confirmationPage"; // Displays a confirmation page with booking info
	    }
	}
