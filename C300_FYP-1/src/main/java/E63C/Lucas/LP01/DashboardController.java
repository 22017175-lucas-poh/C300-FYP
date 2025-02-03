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
    
    @Autowired
    private ConsultantRepository consultantRepository;

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

        // Check if the logged-in member has BO or FA roles
        boolean isBOorFA = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_BO")
                        || authority.getAuthority().equals("ROLE_FA"));

        if (isBOorFA) {
            // Fetch all accounts, cards, consultations, and members for BO/FA
            List<Account> allAccounts = accountRepository.findAll();
            List<Card> allCards = cardRepository.findAll();
            List<Consultation> allConsultations = consultationRepository.findAll();
            List<Consultant> allConsultant = consultantRepository.findAll();
            List<Member> allMembers = memberRepository.findAll();

            // Add attributes to the model for the Admin dashboard
            model.addAttribute("listAccounts", allAccounts);
            model.addAttribute("listCards", allCards);
            model.addAttribute("listConsultations", allConsultations);
            model.addAttribute("listConsultants", allConsultant);
            model.addAttribute("listMembers", allMembers);
            model.addAttribute("member", loggedInMember);

            return "dashboardAdmin"; // Return the Admin dashboard view
        } else {
            // Fetch data for the logged-in user
            List<Card> userCards = cardRepository.findByMember(loggedInMember);
            List<Account> userAccounts = accountRepository.findByMember(loggedInMember);
            List<Consultation> userConsultations = consultationRepository.findByMember(loggedInMember);

            int activeAccountsCount = 0;
            int activeCardsCount = 0;

            for (Account account : userAccounts) {
                if ("ACTIVE".equals(account.getStatus().name())) {
                    activeAccountsCount++;
                }
            }

            // Count active cards
            for (Card card : userCards) {
                if ("APPROVED".equals(card.getStatus().name())) {
                    activeCardsCount++;
                }
            }
            
            // Add attributes to the model for the User dashboard
            model.addAttribute("member", loggedInMember);
            model.addAttribute("listCards", userCards);
            model.addAttribute("listAccounts", userAccounts);
            model.addAttribute("listConsultations", userConsultations);
            model.addAttribute("activeAccountsCount", activeAccountsCount);
            model.addAttribute("activeCardsCount", activeCardsCount);

            return "dashboard"; // Return the User dashboard view
        }
    }
}

