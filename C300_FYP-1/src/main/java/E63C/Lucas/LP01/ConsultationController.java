package E63C.Lucas.LP01;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
            DayOfWeek dayOfWeek = consultationDate.getDayOfWeek();

            // Restrict bookings during weekends
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                model.addAttribute("error", "Consultations cannot be booked on weekends.");
                return "BookConsultation";
            }

            // Check if the user already has 3 consultations on the same day
            List<Consultation> existingConsultations = consultationRepository.findByMemberAndConsultationDate(
                loggedInMember, consultation.getConsultationDate());
            if (existingConsultations.size() >= 3) {
                model.addAttribute("error", "You can only book up to 3 consultations per day.");
                return "BookConsultation";
            }

            consultation.setMember(loggedInMember);
            consultationRepository.save(consultation);

            // Send email to admin after successful booking
            String adminEmail = "musashibestgirl990@gmail.com";
            String subject = "New Consultation Booking";
            String body = "A new consultation has been booked with the following details:\n" +
                "Consultation ID: " + consultation.getId() + "\n" +
                "Consultation Date: " + consultation.getConsultationDate() + "\n" +
                "Consultation Time: " + consultation.getConsultationTime() + "\n" +
                "Consultant Name: " + consultation.getConsultantName() + "\n" +
                "Member Name: " + loggedInMember.getName() + "\n\n" +
                "Please review the booking.";

            sendEmail(adminEmail, subject, body);

            model.addAttribute("consultation", consultation);
            return "Confirmation";
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
        model.addAttribute("consultation", consultation);
        return "EditConsultation";
    }

    @GetMapping("/consultations/delete/{id}")
    public String deleteConsultation(@PathVariable("id") int id) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation Id:" + id));
        consultationRepository.delete(consultation);
        return "redirect:/consultations";
    }

    @PostMapping("/consultations/edit/{id}")
    public String updateConsultation(@PathVariable("id") int id, Consultation updatedConsultation) {
        Consultation existingConsultation = consultationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid consultation ID:" + id));

        existingConsultation.setConsultationDate(updatedConsultation.getConsultationDate());
        existingConsultation.setConsultantName(updatedConsultation.getConsultantName());
        existingConsultation.setConsultationTime(updatedConsultation.getConsultationTime());

        consultationRepository.save(existingConsultation);
        return "redirect:/consultations";
    }
	public void sendEmail(String to, String subject, String body) {
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

    




