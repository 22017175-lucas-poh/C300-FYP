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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * 
 */
@Controller
public class MemberController {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private JavaMailSender javaMailSender;

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
	        return "AddMember";
	    }

	    // Set default role if not provided
	    if (member.getRole() == null || member.getRole().isEmpty()) {
	        member.setRole("ROLE_USER");
	    }

	    // Encode password
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    member.setPassword(passwordEncoder.encode(member.getPassword()));

	    // Set default properties
	    member.setAccountNonLocked(true);
	    member.setFailedAttempt(0);
	    member.setLockTime(null);

	    // Save to generate the `id` first
	    member = memberRepository.save(member);

	    // Generate customId using the generated ID and role
	    String prefix = switch (member.getRole()) {
	        case "ROLE_BO" -> "BO";
	        case "ROLE_FA" -> "FA";
	        default -> "UR";
	    };
	    String customId = prefix + String.format("%05d", member.getId()); // e.g., BO00001
	    member.setCustomId(customId);

	    // Save again with customId
	    memberRepository.save(member);

	    redirectAttribute.addFlashAttribute("success", "Member registered successfully!");
	    return "redirect:/Member";
	}
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("member", new Member());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(RedirectAttributes redirectAttribute, @Valid Member member, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        return "register";
	    }

	    // Check if username exists
	    if (memberRepository.findByUsername(member.getUsername()) != null) {
	        bindingResult.rejectValue("username", "username.exists", "Username already exists");
	        return "register";
	    }

	    // Default role for new members
	    member.setRole("ROLE_USER");

	    // Encode password
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    member.setPassword(passwordEncoder.encode(member.getPassword()));

	    member.setAccountNonLocked(true);
	    member.setFailedAttempt(0);
	    member.setLockTime(null);
	    
	    // Save to generate the `id` first
	    member = memberRepository.save(member);

	    // Generate customId using the generated ID and role
	    String prefix = switch (member.getRole()) {
	        case "ROLE_BO" -> "BO";
	        case "ROLE_FA" -> "FA";
	        default -> "UR";
	    };
	    String customId = prefix + String.format("%05d", member.getId()); // e.g., BO00001
	    member.setCustomId(customId);

	    // Save again with customId
	    memberRepository.save(member);

	    // Send welcome email
	    sendEmail(member.getEmail(), "Welcome to Our Application!", "Dear " + member.getUsername() +
	            ",\n\nThank you for registering with us. Your ID is: " + customId);

	    redirectAttribute.addFlashAttribute("success", "Registration successful! Please log in.");
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
	public String saveUpdatedMember(@PathVariable("id") Integer id, @Valid Member member, BindingResult bindingResult, Model model) {
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("member", memberRepository.getReferenceById(id));
	        return "EditMember";
	    }

	    // Fetch the existing member to ensure immutability for certain fields
	    Member existingMember = memberRepository.getReferenceById(id);

	    // Update mutable fields
	    existingMember.setName(member.getName());
	    existingMember.setUsername(member.getUsername());
	    existingMember.setEmail(member.getEmail());
	    existingMember.setRole(member.getRole());
	    existingMember.setAccountNonLocked(member.isAccountNonLocked());
	    existingMember.setFailedAttempt(member.getFailedAttempt());
	    existingMember.setLockTime(member.getLockTime());

	    // Update password only if provided
	    if (member.getPassword() != null && !member.getPassword().isEmpty()) {
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        existingMember.setPassword(passwordEncoder.encode(member.getPassword()));
	    }

	    // If the role has changed, update the customId
	    if (!existingMember.getRole().equals(member.getRole())) {
	        String prefix = switch (member.getRole()) {
	            case "ROLE_BO" -> "BO";
	            case "ROLE_FA" -> "FA";
	            default -> "UR";
	        };
	        String numericPart = existingMember.getCustomId().substring(2); // Extract the numeric part of the existing customId
	        String updatedCustomId = prefix + numericPart;
	        existingMember.setCustomId(updatedCustomId);
	    }

	    // Save the updated member details
	    memberRepository.save(existingMember);

	    return "redirect:/Member";
	}


	@GetMapping("/Member/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer id) {
		memberRepository.deleteById(id);
		return "redirect:/Member";
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
	
	@GetMapping("/Member/detail")
	public String editLoggedInMember(Model model) {
	    // Get the logged-in username
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();

	    // Fetch the logged-in user's member details
	    Member member = memberRepository.findByUsername(username);

	    if (member == null) {
	        throw new RuntimeException("Member not found!");
	    }

	    // Pass the member details to the view
	    model.addAttribute("member", member);
	    return "EditDetail";
	}
	
	@PostMapping("/Member/detail")
	public String saveUpdatedLoggedInMember(@Valid @ModelAttribute("member") Member updatedMember, 
	                                        BindingResult bindingResult, 
	                                        RedirectAttributes redirectAttributes) {
	    if (bindingResult.hasErrors()) {
	        return "EditDetail"; // Show the form with error messages
	    }

	    // Get the logged-in username
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();

	    // Fetch the existing member
	    Member existingMember = memberRepository.findByUsername(username);
	    if (existingMember == null) {
	        throw new RuntimeException("Member not found!");
	    }

	    // Update fields
	    existingMember.setName(updatedMember.getName());
	    existingMember.setUsername(updatedMember.getUsername());
	    existingMember.setEmail(updatedMember.getEmail());
	    existingMember.setNric(updatedMember.getNric());

	    // Only update password if a new one is provided
	    if (updatedMember.getPassword() != null && !updatedMember.getPassword().isEmpty()) {
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        existingMember.setPassword(passwordEncoder.encode(updatedMember.getPassword()));
	    }

	    // Save the updated member
	    memberRepository.save(existingMember);

	    redirectAttributes.addFlashAttribute("success", "Details updated successfully!");
	    return "redirect:/dashboard";
	}
	
	@PostMapping("/forget")
	public String processForgotPassword(@RequestParam("username") String username, @RequestParam("email") String email,
	                                    Model model, HttpSession session) {
	    // Find the member by username and email
	    Member member = memberRepository.findByUsername(username);
	    if (member == null || !member.getEmail().equals(email)) {
	        model.addAttribute("error", "Invalid username or email");
	        return "forget"; 
	    }
	    model.addAttribute("member", member);
	    Random random = new Random();
	    String otp = String.format("%06d", random.nextInt(1000000));
	 // Store OTP in model 
	    session.setAttribute("otp", otp);
        session.setAttribute("memberId", member.getId()); 
	 // Send OTP email
	    sendEmail(member.getEmail(), "Password Reset OTP", "Dear " + member.getUsername() +
	            ",\n\nYou recently requested to reset your password for RP Digital Bank ."
	            + "\n\nYour One-Time Password (OTP) is: " + otp 
	            + "\n\nPlease enter it on the password reset page."
	            + "\n\nIf you did not request a password reset, please ignore this email."
	            + "\n\nSincerely,\nThe RP Digital Bank Team");
	    return "resetPassword";
	}

    @GetMapping("/forget")
    public String showForgetPasswordPage(Model model) {
        model.addAttribute("member", new Member());
        return "forget";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("enteredOtp") String enteredOtp,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                HttpSession session, Model model) {

        String storedOtp = (String) session.getAttribute("otp");
        Integer memberId = (Integer) session.getAttribute("memberId");

        if (storedOtp == null) { 
            model.addAttribute("otpError", "Invalid or expired OTP.");
            return "resetPassword";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "resetPassword";
        }

        Member member = memberRepository.getReferenceById(memberId);

        if (member == null) {
            model.addAttribute("error", "Invalid member");
            return "forget";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        session.removeAttribute("otp");
        session.removeAttribute("memberId");
        return "redirect:/login";
    }


}
