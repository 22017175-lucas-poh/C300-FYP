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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpServletResponse;
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
	@Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ConsultationRepository consultationRepository;

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

		// Check if username exists
		if (memberRepository.findByUsername(member.getUsername()) != null) {
			bindingResult.rejectValue("username", "username.exists", "Username already exists");
			return "register";
		}

		// Check if NRIC exists
		if (memberRepository.findByNric(member.getNric()) != null) {
			bindingResult.rejectValue("nric", "nric.exists", "NRIC already registered");
			return "register";
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
	public String registerUser(RedirectAttributes redirectAttribute, @Valid Member member,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "register";
		}

		// Check if username exists
		if (memberRepository.findByUsername(member.getUsername()) != null) {
			bindingResult.rejectValue("username", "username.exists", "Username already exists");
			return "register";
		}

		// Check if NRIC exists
		if (memberRepository.findByNric(member.getNric()) != null) {
			bindingResult.rejectValue("nric", "nric.exists", "NRIC already registered");
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
		sendEmail(member.getEmail(), "Welcome to Our Application!",
				"Dear " + member.getUsername() + ",\n\nThank you for registering with us. Your ID is: " + customId);

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
	public String saveUpdatedMember(@PathVariable("id") Integer id, @Valid Member member, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes,
			@RequestParam(value = "newPassword", required = false) String newPassword) { // Get the new password

		if (bindingResult.hasErrors()) {
			model.addAttribute("member", member);
			return "EditMember";
		}

		Member existingMember = memberRepository.getReferenceById(id);
		existingMember.setCustomId(member.getCustomId());
		existingMember.setName(member.getName());
		existingMember.setUsername(member.getUsername());
		existingMember.setEmail(member.getEmail());
		existingMember.setRole(member.getRole());
		member.setAccountNonLocked(true);
		member.setFailedAttempt(0);
		member.setLockTime(null);

		if (newPassword != null && !newPassword.isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			existingMember.setPassword(passwordEncoder.encode(newPassword));
		}

		try {
			memberRepository.save(existingMember);
			redirectAttributes.addFlashAttribute("success", "Details updated successfully!");
			return "redirect:/Member";
		} catch (Exception e) {
			e.printStackTrace(); // Print the exception details to the console
			model.addAttribute("error", "An error occurred during update: " + e.getMessage()); // Optionally display an
																								// error message to the
																								// user
			return "EditMember"; // Or redirect back to the form
		}
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
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "EditDetail";
		}

		// Get the logged-in username
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		// Fetch the existing member
		Member existingMember = memberRepository.findByUsername(username);
		if (existingMember == null) {
			throw new RuntimeException("Member not found!");
		}

		// Check if username exists (exclude current user)
		Member memberWithUsername = memberRepository.findByUsername(updatedMember.getUsername());
		if (memberWithUsername != null && memberWithUsername.getId() != existingMember.getId()) {
			bindingResult.rejectValue("username", "username.exists", "Username already exists");
			return "EditDetail";
		}

		// Check if NRIC exists (exclude current user)
		Member memberWithNric = memberRepository.findByNric(updatedMember.getNric());
		if (memberWithNric != null && memberWithNric.getId() != existingMember.getId()) {
			bindingResult.rejectValue("nric", "nric.exists", "NRIC already registered");
			return "EditDetail";
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
		Member member = memberRepository.findByUsername(username);

		if (member == null || !member.getEmail().equals(email)) {
			model.addAttribute("error", "Invalid username or email");
			return "forget";
		}

		model.addAttribute("member", member);

		// Generate OTP using the utility class
		String otpString = generateOTP();

		// Store OTP and timestamp in the session
		session.setAttribute("generatedOTP", otpString);
		session.setAttribute("otpTimestamp", System.currentTimeMillis());
		session.setAttribute("memberId", member.getId());

		// Send OTP via email
		sendEmail(member.getEmail(), "Password Reset OTP", "Dear " + member.getUsername() + ",\n\n"
				+ "You requested to reset your password for RP Digital Bank.\n\n" + "Your One-Time Password (OTP) is: "
				+ otpString + "\n\n" + "If you did not request this, please ignore this email.\n\n"
				+ "Sincerely,\nThe RP Digital Bank Team");

		return "resetPassword"; // Redirect to OTP entry page
	}

	@GetMapping("/forget")
	public String showForgetPasswordPage(Model model) {
		model.addAttribute("member", new Member());
		return "forget";
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam("enteredOtp") String enteredOtp,
			@RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,
			HttpSession session, Model model) {

		// Remove extra characters & trim spaces
		enteredOtp = enteredOtp.replaceAll("[^0-9]", "").trim();

		System.out.println("Entered OTP in reset (cleaned): " + enteredOtp);
		System.out.println("Session OTP in reset: " + session.getAttribute("generatedOTP"));

		if (!validateOTP(enteredOtp, session)) {
			model.addAttribute("otpError", "Invalid or expired OTP. Try again.");
			return "resetPassword";
		}

		Integer memberId = (Integer) session.getAttribute("memberId");
		Member member = memberRepository.findById(memberId).orElse(null);

		if (member == null) {
			model.addAttribute("error", "Invalid member.");
			return "forget";
		}

		if (!password.equals(confirmPassword)) {
			model.addAttribute("error", "Passwords do not match.");
			return "resetPassword";
		}

		// Update password
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(password));
		memberRepository.save(member);

		session.removeAttribute("generatedOTP");
		session.removeAttribute("otpTimestamp");
		session.removeAttribute("memberId");

		return "redirect:/login";
	}

	private String generateOTP() {
		Random rand = new Random();
		int otp = 100000 + rand.nextInt(900000);
		String otpString = String.valueOf(otp);

		System.out.println("Generated OTP: " + otpString); // Debug log

		return otpString;
	}

	private boolean validateOTP(String otp, HttpSession session) {
		String storedOTP = (String) session.getAttribute("generatedOTP");
		System.out.println("Entered OTP: " + otp);
		System.out.println("Stored OTP: " + storedOTP);
		System.out.println("Session ID: " + session.getId());

		if (storedOTP == null) {
			System.out.println("FAILED: OTP is null.");
			return false;
		}

		if (!storedOTP.trim().equals(otp.trim())) {
			System.out.println("FAILED: OTP does not match.");
			return false;
		}
		return true;
	}

	@GetMapping("/Member/Export")
	public void exportMembersToCsv(HttpServletResponse response) {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=members.csv");

		try (PrintWriter writer = response.getWriter()) {
			/*
			 * List<Member> members = memberRepository.findAll(); // Retrieve all members
			 * int totalMembers = members.size(); // Count total members
			 */
			// Retrieve only members with ROLE_USER
			List<Member> members = memberRepository.findAll().stream()
					.filter(member -> "ROLE_USER".equals(member.getRole())).collect(Collectors.toList());

			int totalMembers = members.size(); // Count the total users
			// Write CSV headers (matching the View Members page)
			writer.println("ID,Member,Username,Email,NRIC (Last 4 Digits),Role");

			for (Member member : members) {
				String nricMasked = member.getNric().substring(member.getNric().length() - 4); // Show only last 4 digits
																								// of NRIC
				writer.printf("%s,%s,%s,%s,%s,%s%n", 
						member.getCustomId(), 
						member.getName(), 
						member.getUsername(),
						member.getEmail(), 
						nricMasked, 
						member.getRole());
			}

			// Add a summary row with the total count
			writer.println();
			writer.printf("Total Members Created:,%d%n", totalMembers);

			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@GetMapping("/Admin/Member/Report/{id}")
	public ResponseEntity<Void> exportMemberReport(@PathVariable int id, HttpServletResponse response) {
	    try {
	        // Set response headers
	        response.setContentType("text/csv");
	        response.setHeader("Content-Disposition", "attachment; filename=member_report.csv");

	        // Fetch user details
	        Member member = memberRepository.findById(id);

	        // Fetch related data
	        List<Account> accounts = accountRepository.findByMember(member);
	        List<Card> cards = cardRepository.findByMember(member);
	        List<Consultation> consultations = consultationRepository.findByMember(member);

	        PrintWriter writer = response.getWriter();

	        // Write Member Details
	        writer.println("Member Report for: " + member.getName());
	        writer.println("Username,Email,Role");
	        writer.printf("%s,%s,%s%n", member.getUsername(), member.getEmail(), member.getRole());

	        writer.println();

	        // Write Accounts
	        writer.println("Accounts");
	        writer.println("Account Type,Account Number,Status");
	        for (Account account : accounts) {
	            writer.printf("%s,%s,%s%n",
	                account.getAccountType().getName(),
	                account.getAccountNumber().substring(account.getAccountNumber().length() - 4),
	                account.getStatus());
	        }

	        writer.println();

	        // Write Cards
	        writer.println("Cards");
	        writer.println("Card Type,Card Number,Status");
	        for (Card card : cards) {
	            writer.printf("%s,%s,%s%n",
	                card.getCardType().getName(),
	                card.getCardNumber(),
	                card.getStatus());
	        }

	        writer.println();

	        // Write Consultations
	        writer.println("Consultations");
	        writer.println("Date,Time,Consultant");
	        for (Consultation consultation : consultations) {
	            writer.printf("%s,%s,%s%n",
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
