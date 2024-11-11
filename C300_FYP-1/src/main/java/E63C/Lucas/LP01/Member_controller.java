/**
 * 
 * I declare that this code was written by me, 22044153. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Muhammad Lutfil Hadi Bin Rosli
 * Student ID: 22044153
 * Class: E63C
 * Date created: 2024-Nov-10 9:21:00 pm 
 * 
 */

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
public class Member_controller {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/Member")
    public String ViewMembers(Model model) {

        List<Member> listMember = memberRepository.findAll();

        model.addAttribute("listMember", listMember);

        return "ViewMember"; 
    }

    @GetMapping("/Member/add")
    public String addMember(Model model) {
        model.addAttribute("member", new Member());
        return "AddMember"; 
    }

    @PostMapping("/Member/save")
    public String saveMember(Member member, Model model) {

        // Check for duplicate account ID (assuming accountId is unique)
        Member existingMember = memberRepository.findByAccountId(member.getAccountID());
        if (existingMember != null) {
            model.addAttribute("duplicateAccountIdError", true);
            return "AddMember"; // Return to the AddMember page 
        }

        memberRepository.save(member);
        return "redirect:/Member"; // Successful save, redirect to ViewMembers page
    }
}
