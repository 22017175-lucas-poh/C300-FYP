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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * 
 */
@Controller
public class MemberController {

	@Autowired
	private MemberRepository memberRepository;
	
	@GetMapping("/Member")
	public String viewMembers(Model model) {
		List<Member> listmembers= memberRepository.findAll();
		
		model.addAttribute("listmembers",listmembers);
		return"view_members";		
	}
	@GetMapping("/Member/add")
	public String addMember(Model model) {
		model.addAttribute("member", new Member	());
		return"add_Member";
	}
	@PostMapping("/Member/save")
	public String saveMember(@Valid Member member, BindingResult bindingResult, RedirectAttributes redirectAttribute, Model model) {
	    if (bindingResult.hasErrors()) {
	        // Validation errors occurred, return to the add page with error messages
	        List<Member> memberList = memberRepository.findAll();
	        model.addAttribute("memberList", memberList);
	        return "add_Member";
	    }

	    // Validation successful, proceed with saving the member
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    String encodedPassword = passwordEncoder.encode(member.getPassword());
	    member.setPassword(encodedPassword);
	    member.setRole("ROLE_USER");
	    member.setAccountNonLocked(true);

	    memberRepository.save(member);
	    redirectAttribute.addFlashAttribute("success", "Member registered");
	    return "redirect:/Member";
	}

	//edit
	
	@GetMapping("/Member/edit/{id}")
	public String editMember(@PathVariable("id") Integer id,Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member",member);
		
		return"edit_Member";
	}
	@PostMapping("/Member/edit/{id}")
	public String saveUpdatedMember(@PathVariable("id") Integer id,Member member){
		memberRepository.save(member);
		return "redirect:/Member";
	}
	@GetMapping("/Member/delete/{id}")
	public String deleteMember(@PathVariable("id")Integer id ) {
		memberRepository.deleteById(id);
		
		return "redirect:/Member";
	}
	
	
	
	
}
