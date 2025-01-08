/**
 * 
 * I declare that this code was written by me, 22044153. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Muhammad Lutfil Hadi Bin Rosli
 * Student ID: 22044153
 * Class: E63C
 * Date created: 2024-Dec-17 1:13:05 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 */
@Controller
public class DashboardController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        // Get the current logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch the logged-in member based on the username
        Member loggedInMember = memberRepository.findByUsername(username);
        if (loggedInMember == null) {
            return "redirect:/login"; // Redirect to login if member is not found
        }

        // Fetch related data
        List<Card> userCards = cardRepository.findByMember(loggedInMember);
        List<Consultation> userConsultations = consultationRepository.findByMember(loggedInMember);

        // Add attributes to the model for display on the dashboard
        model.addAttribute("member", loggedInMember);
        model.addAttribute("listCards", userCards);
        model.addAttribute("listConsultations", userConsultations);

        return "Dashboard"; // Return the dashboard view
    }

}
