package E63C.Lucas.LP01;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConsultationController {

    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConsultantRepository consultantRepository;

    @GetMapping("/consultations")
    public String viewConsultations(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Member loggedInMember = memberRepository.findByUsername(username);

        if (loggedInMember != null) {
            List<Consultation> listConsultations = consultationRepository.findByMember(loggedInMember);
            model.addAttribute("listConsultations", listConsultations);
        } else {
            model.addAttribute("listConsultations", List.of());
        }

        return "ViewConsultation";
    }

    @GetMapping("/consultations/book")
    public String addBooking(Model model) {
        List<Consultant> consultants = consultantRepository.findAll();
        List<Consultant> activeConsultants = consultants.stream()
            .filter(consultant -> consultant.getDateLeft() == null)
            .toList();

        model.addAttribute("consultants", activeConsultants);
        model.addAttribute("consultation", new Consultation());

        return "BookConsultation";
    }

    @PostMapping("/consultations/book/save")
    public String saveBooking(Consultation consultation, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Member loggedInMember = memberRepository.findByUsername(username);

        if (loggedInMember != null) {
            LocalDate consultationDate = consultation.getConsultationDate().toLocalDate();
            DayOfWeek dayOfWeek = consultationDate.getDayOfWeek();

            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                model.addAttribute("error", "Consultations cannot be booked on weekends.");
                return "BookConsultation";
            }

            List<Consultation> existingConsultations = consultationRepository.findByMemberAndConsultationDate(
                loggedInMember, consultation.getConsultationDate());
            if (existingConsultations.size() >= 3) {
                model.addAttribute("error", "You can only book up to 3 consultations per day.");
                return "BookConsultation";
            }

            List<Consultation> overlappingConsultations = consultationRepository.findByConsultantNameAndConsultationDateAndConsultationTime(
                consultation.getConsultantName(), consultation.getConsultationDate(), consultation.getConsultationTime());

            if (!overlappingConsultations.isEmpty()) {
                model.addAttribute("error", "This time slot is already booked with the selected consultant. Please choose a different time.");
                return "BookConsultation";
            }

            consultation.setMember(loggedInMember);
            consultationRepository.save(consultation);

            model.addAttribute("consultation", consultation);
            return "Confirmation";
        }

        return "redirect:/consultations";
    }

    @PostMapping("/consultations/edit/{id}")
    public String updateConsultation(@PathVariable("id") int id, Consultation updatedConsultation, Model model) {
        Consultation existingConsultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation ID:" + id));

        List<Consultation> overlappingConsultations = consultationRepository
            .findByConsultantNameAndConsultationDateAndConsultationTime(
                updatedConsultation.getConsultantName(), 
                updatedConsultation.getConsultationDate(), 
                updatedConsultation.getConsultationTime()
            );

        // Exclude the current consultation from overlap check
        overlappingConsultations.removeIf(c -> c.getId() == id);

        // Error handling for overlapping consultations
        if (!overlappingConsultations.isEmpty()) {
            model.addAttribute("error", "This time slot is already booked with the selected consultant.");
            model.addAttribute("consultation", existingConsultation);
            model.addAttribute("consultants", consultantRepository.findAll());  
            model.addAttribute("existingConsultations", consultationRepository.findByConsultationDate(updatedConsultation.getConsultationDate()));
            return "EditConsultation";
        }

        // Proceed if no overlap
        existingConsultation.setConsultationDate(updatedConsultation.getConsultationDate());
        existingConsultation.setConsultantName(updatedConsultation.getConsultantName());
        existingConsultation.setConsultationTime(updatedConsultation.getConsultationTime());

        consultationRepository.save(existingConsultation);
        return "redirect:/consultations";
    }


    @GetMapping("/Admin/Consultations")
    public String viewAllConsultations(Model model) {
        List<Consultation> listAllConsultations = consultationRepository.findAll();
        model.addAttribute("listAllConsultations", listAllConsultations);
        return "AdminViewConsultations";
    }

    @GetMapping("/consultations/edit/{id}")
    public String editConsultation(@PathVariable("id") int id, Model model) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));

        List<Consultant> activeConsultants = consultantRepository.findAll()
            .stream()
            .filter(consultant -> consultant.getDateLeft() == null)
            .toList();

        // Get existing consultations for the same date (excluding the current one)
        List<Consultation> existingConsultations = consultationRepository
            .findByConsultationDate(consultation.getConsultationDate())
            .stream()
            .filter(c -> c.getId() != id) 
            .toList();

        model.addAttribute("consultation", consultation);
        model.addAttribute("consultants", activeConsultants);
        model.addAttribute("existingConsultations", existingConsultations);  // Add this line

        return "EditConsultation";
    }

    @GetMapping("/consultations/delete/{id}")
    public String deleteConsultation(@PathVariable("id") int id) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));
        consultationRepository.delete(consultation);
        return "redirect:/consultations";
    }
}



    




