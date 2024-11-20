package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	    List<Consultation> listConsultations = consultationRepository.findAll();
	    model.addAttribute("listConsultations", listConsultations);
	    return "ViewConsultation"; // Ensure this matches your HTML file name
	}

    @GetMapping("/consultations/book")
    public String AddBooking(Model model) {
        model.addAttribute("consultation", new Consultation());
        return "BookConsultation"; // Renders the booking form
    }
    @PostMapping("/consultations/book/save")
    public String saveBooking(Consultation consultation, int memberId, Model model) {
        // Fetch the Member from the database
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

        // Set the Member in the Consultation entity
        consultation.setMember(member);

        // Save the consultation
        consultationRepository.save(consultation);

        // Redirect to View Consultations
        return "redirect:/consultations";
    }

}

