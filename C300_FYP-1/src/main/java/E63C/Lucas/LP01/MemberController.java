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
import org.springframework.web.bind.annotation.ModelAttribute;
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
		List<Member> listMembers = memberRepository.findAll();
		model.addAttribute("listMembers", listMembers);
		return "ViewMember";
	}

	@GetMapping("/Member/add")
	public String addMember(Model model) {
		model.addAttribute("member", new Member());
		return "AddMember";
	}

	@PostMapping("/Member/save")
	public String saveMember(RedirectAttributes redirectAttribute, @Valid Member member, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			System.out.println("error");
			return "AddMember";
		}

		// Validation successful, proceed with saving the member
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);

		member.setAccountNonLocked(true);
		member.setFailedAttempt(0);
		member.setLockTime(null);
		memberRepository.save(member);

		redirectAttribute.addFlashAttribute("success", "Member registered!");
		return "redirect:/Member";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("member", new Member());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("member") @Valid Member member, BindingResult result,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return "register";
		}
		if (memberRepository.findByUsername(member.getUsername()) != null) {
			result.rejectValue("username", "username.exists", "Username already exists");
			return "register";
		}

		member.setRole("ROLE_USER");
		member.setAccountNonLocked(true);
		member.setFailedAttempt(0);
		member.setLockTime(null);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(member.getPassword());
		member.setPassword(encodedPassword);
		memberRepository.save(member);

		redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
		return "redirect:/login";
	}
	
	// edit
	@GetMapping("/Member/edit/{id}")
	public String editMember(@PathVariable("id") Integer id, Model model) {
		Member member = memberRepository.getReferenceById(id);
		model.addAttribute("member", member);
		return "EditMember";
	}

	@PostMapping("/Member/edit/{id}")
	public String saveUpdatedMember(@PathVariable("id") Integer id, @Valid Member member, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			// Handle validation errors (return to the edit page with errors)
			return "EditMember"; // Ensure to return the correct view with errors
		}
		// Retrieve the existing member from the repository
		Member existingMember = memberRepository.getReferenceById(id);
		member.setAccountNonLocked(true);
		member.setFailedAttempt(0);
		member.setLockTime(null);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
		// Save the updated member
		memberRepository.save(member);
		return "redirect:/Member";
	}

	@GetMapping("/Member/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		memberRepository.deleteById(id);
		return "redirect:/Member";
	}
	
	@GetMapping("/forget")
	public String showForgetpassword(Model model) {
		model.addAttribute("member", new Member());
		return "forget";
	}
}
