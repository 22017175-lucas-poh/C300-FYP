package E63C.Lucas.LP01;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/Admin/consultations")
    public String viewAllConsultations(Model model) {
        // Fetch all consultations
        List<Consultation> listAllConsultations = consultationRepository.findAll();
        model.addAttribute("listAllConsultations", listAllConsultations);
        return "AdminViewConsultations";
    }
    @PreAuthorize("hasRole('ADMIN')") // Restrict access to Admin only
    @GetMapping("/consultations/edit/{id}")
    public String editConsultation(@PathVariable("id") int id, Model model) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));
        model.addAttribute("consultation", consultation);
        return "EditConsultation"; // Render edit consultation form
    }

    @PreAuthorize("hasRole('ADMIN')") // Restrict access to Admin only
    @GetMapping("/consultations/delete/{id}")
    public String deleteConsultation(@PathVariable("id") int id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));
        consultationRepository.delete(consultation);
        return "redirect:/Admin/consultations"; // Redirect to admin consultations view
    }
    @PostMapping("/consultations/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateConsultation(@PathVariable("id") int id, Consultation updatedConsultation) {
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid consultation ID:" + id));

        // Update consultation details
        existingConsultation.setConsultationDate(updatedConsultation.getConsultationDate());
        existingConsultation.setConsultantName(updatedConsultation.getConsultantName());

        consultationRepository.save(existingConsultation); // Save updated consultation
        return "redirect:/Admin/consultations"; // Redirect to the admin view
    }

}
    




