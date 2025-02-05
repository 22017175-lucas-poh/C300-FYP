package E63C.Lucas.LP01;

import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Autowired
    private JavaMailSender javaMailSender;

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
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = consultationDate.getDayOfWeek();

            // Check if the consultation date is in the past
            if (consultationDate.isBefore(today)) {
                model.addAttribute("error", "Consultations cannot be booked for past dates. Please choose a future date.");
                return "BookConsultation";
            }

            // Prevent booking on weekends
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                model.addAttribute("error", "Consultations cannot be booked on weekends. Please select a weekday.");
                return "BookConsultation";
            }

            // Limit the number of consultations per day
            List<Consultation> existingConsultations = consultationRepository.findByMemberAndConsultationDate(
                loggedInMember, consultation.getConsultationDate());
            if (existingConsultations.size() >= 3) {
                model.addAttribute("error", "You can only book up to 3 consultations per day.");
                return "BookConsultation";
            }

            // Check for overlapping consultations with the same consultant
            List<Consultation> overlappingConsultations = consultationRepository
                .findByConsultantNameAndConsultationDateAndConsultationTime(
                    consultation.getConsultantName(),
                    consultation.getConsultationDate(),
                    consultation.getConsultationTime()
                );

            if (!overlappingConsultations.isEmpty()) {
                model.addAttribute("error", "This time slot is already booked with the selected consultant. Please choose a different time.");
                return "BookConsultation";
            }

            // ✅ Save the consultation if all checks pass
            consultation.setMember(loggedInMember);
            consultationRepository.save(consultation);

            model.addAttribute("consultation", consultation);
            return "Confirmation";
        }

        return "redirect:/consultations";
    }


    @PostMapping("/consultations/edit/{id}")
    public String updateConsultation(@PathVariable("id") int id, Consultation updatedConsultation, Model model) {
        // Get the existing consultation
        Consultation existingConsultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation ID:" + id));

        // Check for overlapping consultations
        List<Consultation> overlappingConsultations = consultationRepository
            .findByConsultantNameAndConsultationDateAndConsultationTime(
                updatedConsultation.getConsultantName(),
                updatedConsultation.getConsultationDate(),
                updatedConsultation.getConsultationTime()
            );

        // Remove the current consultation if it is being updated
        overlappingConsultations.removeIf(c -> c.getId() == id);

        // Check if the logged-in member is valid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member loggedInMember = memberRepository.findByUsername(username);

        if (loggedInMember != null) {
            LocalDate consultationDate = updatedConsultation.getConsultationDate().toLocalDate();
            LocalDate today = LocalDate.now();
            DayOfWeek dayOfWeek = consultationDate.getDayOfWeek();

            // Check for overlapping consultations
            if (!overlappingConsultations.isEmpty()) {
                model.addAttribute("error", "This time slot is already booked with the selected consultant.");
                model.addAttribute("consultation", existingConsultation);
                model.addAttribute("consultants", consultantRepository.findAll());
                model.addAttribute("existingConsultations", consultationRepository.findByConsultationDate(updatedConsultation.getConsultationDate()));
                return "EditConsultation";
            }

            // Ensure consultations are not booked for past dates
            if (consultationDate.isBefore(today)) {
                model.addAttribute("error", "Consultations cannot be booked for past dates. Please choose a future date.");
                model.addAttribute("consultation", existingConsultation);  // Keep existing consultation for editing
                return "EditConsultation";
            }

            // Proceed to update the consultation details
            existingConsultation.setConsultationDate(updatedConsultation.getConsultationDate());
            existingConsultation.setConsultantName(updatedConsultation.getConsultantName());
            existingConsultation.setConsultationTime(updatedConsultation.getConsultationTime());

            consultationRepository.save(existingConsultation);

            // Get the email from the Member object
            Member member = existingConsultation.getMember();
            if (member == null) {
                model.addAttribute("error", "No member associated with this consultation.");
                return "EditConsultation";
            }

            String customerEmail = member.getEmail(); // Retrieve the email

            // Prepare email details
            String subject = "Consultation Updated Successfully";
            String body = "Dear " + member.getName() + ",\n\n"
                    + "Your consultation has been updated with the following details:\n"
                    + "Consultant Name: " + existingConsultation.getConsultantName() + "\n"
                    + "Consultation Date: " + existingConsultation.getConsultationDate() + "\n"
                    + "Consultation Time: " + existingConsultation.getConsultationTime() + "\n\n"
                    + "Please review the updated consultation details. If there are any issues, please contact us.";

            // Send email to the member
            try {
                sendEmail(customerEmail, subject, body);  
            } catch (Exception e) {
                model.addAttribute("error", "Failed to send email: " + e.getMessage());
                return "redirect:/consultations";
            }
        } else {
            model.addAttribute("error", "You must be logged in to update a consultation.");
            return "redirect:/consultations";
        }

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
    @GetMapping("/Admin/Consultations/Export")
    public ResponseEntity<Void> exportConsultationsToCsv(HttpServletResponse response) {
        try {
            // Set response headers
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=consultations.csv");

            // Retrieve consultation data
            List<Consultation> consultations = consultationRepository.findAll(); // Replace with consultationService if applicable
            int consultationCount = consultations.size(); // Count the number of consultations

            // Write CSV data
            PrintWriter writer = response.getWriter();
            writer.println("Member Name,Consultation Date,Consultation Time,Consultant Name");

            for (Consultation consultation : consultations) {
                writer.printf("%s,%s,%s,%s%n",
                    consultation.getMember().getName(),
                    consultation.getConsultationDate(),
                    consultation.getConsultationTime(),
                    consultation.getConsultantName());
            }

            // Add a summary row with the count
            writer.println();
            writer.printf("Total Consultations:,%d%n", consultationCount); // Summary row

            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }



    @GetMapping("/consultations/delete/{id}")
    public String deleteConsultation(@PathVariable("id") int id) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));
        consultationRepository.delete(consultation);
        return "redirect:/consultations";
    }
    
	private void sendEmail(String to, String subject, String body) {
	    try {
	        SimpleMailMessage msg = new SimpleMailMessage();
	        msg.setTo(to);
	        msg.setSubject(subject);
	        msg.setText(body);
	        msg.setFrom("musashibestgirl990@gmail.com");

	        javaMailSender.send(msg);
	        System.out.println("Email sent successfully to: " + to);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    
    
}



    




