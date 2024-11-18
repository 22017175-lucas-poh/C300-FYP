/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Jan-12 3:27:47 pm 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/login")
    public String loginPage(
            HttpServletRequest request,
            Model model
    ) {
        String error = request.getParameter("error");
        String username = request.getParameter("username");

        if (error != null) {
            // Failed authentication attempt
            Member member = memberRepository.findByUsername(username);
            if (member != null && !member.isAccountNonLocked()) {
                // Account is locked due to multiple failed attempts
                model.addAttribute("error", "Account is locked. Please contact support.");
            } else {
                model.addAttribute("error", "Invalid username or password.");
            }
        }

        return "login";
    }
}
