package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
		List<Consultation> listconsultations= consultationRepository.findAll();
		
		model.addAttribute("listconsultations",listconsultations);
		return"view_consultations";		
	}

    @GetMapping("/consultations/book")
    public String AddBooking(Model model) {
        model.addAttribute("consultation", new Consultation());
        return "BookConsultation"; // Renders the booking form
    }
    @PostMapping("/consultations/book/save")
    public String SaveBooking(Consultation consultation, Model model) {
    	consultationRepository.save(consultation);
		return "redirect:/Account_type";
	}
	
	
	
	
}
