package E63C.Lucas.LP01;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletResponse;

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
            LocalDate currentDate = LocalDate.now();
            LocalDate consultationDate = consultation.getConsultationDate().toLocalDate();
            DayOfWeek dayOfWeek = consultationDate.getDayOfWeek();

            // Fetch active consultants to repopulate the dropdown in case of error
            List<Consultant> activeConsultants = consultantRepository.findAll()
                .stream()
                .filter(consultant -> consultant.getDateLeft() == null)
                .toList();
            model.addAttribute("consultants", activeConsultants);

            // Prevent booking for past dates
            if (consultationDate.isBefore(currentDate)) {
                model.addAttribute("error", "You cannot book a consultation for a past date.");
                return "BookConsultation";
            }

            // Prevent booking on weekends
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                model.addAttribute("error", "Consultations cannot be booked on weekends.");
                return "BookConsultation";
            }

            // Enforce global consultation limit (max 3 per day)
            int totalConsultationsForDay = consultationRepository.countByConsultationDate(consultation.getConsultationDate());
            if (totalConsultationsForDay >= 3) {
                model.addAttribute("error", "Only 3 consultations are allowed per day. Please choose a different date.");
                return "BookConsultation";
            }



            // Prevent overlapping consultations for the user
            List<Consultation> userConflictingConsultations = consultationRepository.findByMemberAndConsultationDateAndConsultationTime(
                loggedInMember, consultation.getConsultationDate(), consultation.getConsultationTime());
            if (!userConflictingConsultations.isEmpty()) {
                model.addAttribute("error", "You already have a consultation booked at this time.");
                return "BookConsultation";
            }

            // Save the consultation
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
    
    @GetMapping("/Admin/Consultations/Export")
    public ResponseEntity<Void> exportConsultationsToCsv(HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=consultations.csv");

            List<Consultation> consultations = consultationRepository.findAll();
            PrintWriter writer = response.getWriter();
            writer.println("Member Name,Consultation Date,Consultation Time,Consultant Name");

            for (Consultation consultation : consultations) {
                writer.printf("%s,%s,%s,%s%n",
                    consultation.getMember().getName(),
                    consultation.getConsultationDate(),
                    consultation.getConsultationTime(),
                    consultation.getConsultantName());
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }
} 




    




