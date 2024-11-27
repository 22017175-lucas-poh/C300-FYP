package E63C.Lucas.LP01;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 */
@Controller
public class ConsultationController {

	@Autowired
	private ConsultationRepository consultationRepository;
	@Autowired
	private MemberRepository memberRepository;
	
    @GetMapping("/consultations")
    public String viewConsultations(Model model) {
        // Get the logged-in user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Find the logged-in member
        Member loggedInMember = memberRepository.findByUsername(username);

        if (loggedInMember != null) {
            // Fetch consultations for the logged-in member
            List<Consultation> listConsultations = consultationRepository.findByMember(loggedInMember);
            model.addAttribute("listConsultations", listConsultations);
        } else {
            // Handle the case where the user is not found
            model.addAttribute("listConsultations", List.of());
        }

        return "ViewConsultation";
    }

    @GetMapping("/consultations/book")
    public String AddBooking(Model model) {
        model.addAttribute("consultation", new Consultation());
        return "BookConsultation"; // Renders the booking form
    }
    @PostMapping("/consultations/book/save")
    public String saveBooking(Consultation consultation, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Member loggedInMember = memberRepository.findByUsername(username);

        if (loggedInMember != null) {
            consultation.setMember(loggedInMember);
            consultation = consultationRepository.save(consultation);

            // Pass the saved consultation to the confirmation view
            model.addAttribute("consultation", consultation);
            return "Confirmation";
        }

        return "redirect:/consultations";
    }
    @GetMapping("/Admin/Consultations")
    public String viewAllConsultations(Model model) {
        // Fetch all consultations
        List<Consultation> listAllConsultations = consultationRepository.findAll();
        model.addAttribute("listAllConsultations", listAllConsultations);
        return "AdminViewConsultations";
    }
}
    




