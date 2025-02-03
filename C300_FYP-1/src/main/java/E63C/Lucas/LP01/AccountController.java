	/**
	 * 
	 * I declare that this code was written by me, Lenovo. 
	 * I will not copy or allow others to copy my code. 
	 * I understand that copying code is considered as plagiarism.
	 * 
	 * Student Name: Lucas Poh Zhan Le
	 * Student ID: 22017175
	 * Class: E63C
	 * Date created: 2024-Dec-24 3:40:42 pm 
	 * 
	 */
	
	package E63C.Lucas.LP01;
	
	import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
	
	/**
	 * Controller for managing Account entities.
	 */
	@Controller
	public class AccountController {
	
		@Autowired
		private AccountRepository accountRepository;
		@Autowired
		private MemberRepository memberRepository;
		@Autowired
		private AccountTypeRepository accountTypeRepository;
	    @Autowired
	    private JavaMailSender javaMailSender;
	
		/**
		 * View all Accounts.
		 * 
		 * @param model Thymeleaf model to add attributes for the view
		 * @return Thymeleaf template name
		 */
		@GetMapping("/Account")
		public String viewAccounts(Model model) {
			// Get the currently logged-in user's ID
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String loggedInUsername = authentication.getName(); // Assuming username is unique
			Integer loggedInUserId = getCurrentUserId(loggedInUsername); // Implement this method to map username to
																			// member_id
	
			// Fetch accounts only for the logged-in user
			List<Account> listAccounts = accountRepository.findByMemberId(loggedInUserId);
			model.addAttribute("listAccounts", listAccounts); // Add accounts to the model
	
			return "ViewAccount"; // Thymeleaf template for displaying accounts
		}
	
		private Integer getCurrentUserId(String username) {
			// Implement logic to fetch the member_id of the logged-in user from your
			// database
			return memberRepository.findByUsername(username).getId();
		}
	
		/**
		 * Display the form to add a new Account.
		 * 
		 * @param model Thymeleaf model to add attributes for the view
		 * @return Thymeleaf template name
		 */
		@GetMapping("/Account/add")
		public String addAccountForm(Model model) {
			model.addAttribute("account", new Account()); // Create an empty Account object for the form
			model.addAttribute("accountTypes", accountTypeRepository.findAll()); // Fetch account types
			return "AddAccount"; // Thymeleaf template for adding a new account
		}
	
		/**
		 * Save a new Account to the database.
		 * 
		 * @param account The Account object populated from the form
		 * @param model   Thymeleaf model to add attributes for the view
		 * @return Redirect to the accounts list or back to the form in case of errors
		 */
		@PostMapping("/Account/save")
		public String saveAccount(
		        @RequestParam("passportImage") MultipartFile passportImage, // Get the image file
		        Account account,
		        Model model) {

		    // Get the currently logged-in user's ID
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    String loggedInUsername = authentication.getName();
		    Integer loggedInUserId = getCurrentUserId(loggedInUsername);

		    // Fetch the Member entity
		    Member member = memberRepository.findById(loggedInUserId).orElse(null);
		    if (member == null) {
		        model.addAttribute("error", "Member not found.");
		        return "redirect:/Account";
		    }

		    // Set default values for the account
		    account.setStatus(Account.AccountStatus.PENDING);
		    account.setAccountNumber(generateRandomAccountNumber());
		    account.setCreationDate(Date.valueOf(LocalDate.now()));
		    account.setBalance(0.0);
		    account.setBankName("RP Digital Bank");
		    account.setMember(member);

		    accountRepository.save(account);

		    // Prepare email details
		    String subject = "New Account Application Received";
		    String body = "<p>A new account has been applied with the following details:</p>"
		            + "<p><strong>Account Number:</strong> " + account.getAccountNumber() + "</p>"
		            + "<p><strong>Account Type:</strong> " + account.getAccountType().getName() + "</p>"
		            + "<p><strong>Holder Name:</strong> " + account.getHolderName() + "</p>"
		            + "<p><strong>Bank Name:</strong> " + account.getBankName() + "</p>"
		            + "<p>Please review this application.</p>";

		    // Send email with the uploaded passport image
		    try {
		        sendEmailWithInlineImage("musashibestgirl990@gmail.com", subject, body, passportImage);
		    } catch (Exception e) {
		        model.addAttribute("error", "Failed to send email: " + e.getMessage());
		        return "redirect:/Account";
		    }

		    return "redirect:/Account";
		}

		private void sendEmailWithInlineImage(String to, String subject, String body, MultipartFile imageFile) throws MessagingException, IOException {
		    MimeMessage message = javaMailSender.createMimeMessage();
		    MimeMessageHelper helper = new MimeMessageHelper(message, true);

		    helper.setTo(to);
		    helper.setSubject(subject);
		    helper.setFrom("musashibestgirl990@gmail.com");
		    helper.setText(body, true); // Enable HTML content for body

		    if (imageFile != null && !imageFile.isEmpty()) {
		        String contentId = "passportImage";  // Unique Content-ID for the image

		        // Add the image as an inline attachment
		        helper.addInline(contentId, new ByteArrayResource(imageFile.getBytes()), imageFile.getContentType());

		        // Update the email body to reference the inline image
		        String imageTag = "<p><strong>Uploaded Passport Image:</strong></p>"
		                + "<img src='cid:" + contentId + "' style='width:300px;height:auto;' />";
		        helper.setText(body + imageTag, true);
		    }

		    // Send the email
		    javaMailSender.send(message);
		    System.out.println("Email sent successfully with inline image to: " + to);
		}






		// Helper method to generate random account number
		private String generateRandomAccountNumber() {
			return String.valueOf((long) (Math.random() * 1000000000000L)); // Random 12-digit number
		}
	
		/**
		 * Display the form to edit an existing Account.
		 * 
		 * @param id    ID of the account to edit
		 * @param model Thymeleaf model to add attributes for the view
		 * @return Thymeleaf template name or redirect if not found
		 */
		@GetMapping("/Account/edit/{id}")
		public String editAccountForm(@PathVariable("id") Integer id, Model model) {
			Account account = accountRepository.findById(id).orElse(null); // Find account by ID
			if (account == null) {
				model.addAttribute("error", "Account not found."); // Error message
				return "redirect:/Account"; // Redirect if account not found
			}
			model.addAttribute("account", account); // Add account to the model for editing
			return "EditAccount"; // Thymeleaf template for editing the account
		}
	
		/**
		 * Save updates to an existing Account.
		 * 
		 * @param id             ID of the account to update
		 * @param updatedAccount Account object with updated details
		 * @param model          Thymeleaf model to add attributes for the view
		 * @return Redirect to accounts list or return to edit form in case of errors
		 */
		@PostMapping("/Account/edit/{id}")
		public String saveUpdatedAccount(@PathVariable("id") Integer id, Account updatedAccount, Model model) {
			Account existingAccount = accountRepository.findById(id).orElse(null); // Find account by ID
	
			if (existingAccount == null) {
				model.addAttribute("error", "Account not found."); // Error message
				return "redirect:/Account"; // Redirect if account not found
			}
	
			// Check for duplicate account name
			if (!existingAccount.getAccountName().equals(updatedAccount.getAccountName())) {
				List<Account> duplicateAccounts = accountRepository.findByAccountName(updatedAccount.getAccountName());
				if (!duplicateAccounts.isEmpty()) {
					model.addAttribute("duplicateAccountError", "Account name already exists."); // Error message
					model.addAttribute("account", updatedAccount); // Retain entered data
					return "EditAccount"; // Return to the edit form
				}
			}
	
			// Update account fields
			existingAccount.setAccountName(updatedAccount.getAccountName());
			existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
			existingAccount.setBalance(updatedAccount.getBalance());
			existingAccount.setCreationDate(updatedAccount.getCreationDate());
			existingAccount.setBankName(updatedAccount.getBankName());
			existingAccount.setStatus(updatedAccount.getStatus());
			existingAccount.setAccountType(updatedAccount.getAccountType());
			existingAccount.setMember(updatedAccount.getMember());
	
			accountRepository.save(existingAccount); // Save the updated account
	
			return "redirect:/Account"; // Redirect to the accounts list
		}
	
		@GetMapping("/Account/cancelPage")
		public String showCancelPage(HttpSession session, Model model) {
			Integer accountId = (Integer) session.getAttribute("accountId");
	
			if (accountId != null) {
				Account account = accountRepository.findById(accountId).orElse(null);
				if (account != null) {
					model.addAttribute("account", account);
					return "cancelAccountPage"; // The cancel account page view
				}
			}
	
			model.addAttribute("message", "Session expired or invalid account details.");
			return "errorPage"; // Redirect to an error page
		}
	
		@PostMapping("/Account/cancel/{id}")
		public String cancelAccount(@PathVariable("id") Integer accountId, Model model, HttpSession session) {
			Account account = accountRepository.findById(accountId).orElse(null);
	
			if (account != null) {
				// Get the current logged-in user
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				String username = authentication.getName(); // Get the current username
	
				// Fetch the member from the repository
				Member member = memberRepository.findByUsername(username);
	
				// Generate OTP
				String otpString = generateOTP();
	
				// Store the OTP and accountId in the session
				session.setAttribute("generatedOTP", otpString);
				session.setAttribute("otpTimestamp", System.currentTimeMillis());
				session.setAttribute("accountId", accountId); // Save accountId in session
	
				// Send OTP to the member's email
				String customerEmail = member.getEmail(); // Get the email from the member object
				String subject = "Your OTP for Account Cancellation Request";
				String body = "Dear customer,\n\n"
						+ "You requested to cancel your account. Please use the following OTP to confirm your request:\n"
						+ "OTP: " + otpString + "\n\n" + "If you did not request this, please ignore this email.";
	
				sendEmail(customerEmail, subject, body); // Your method to send the email
	
				// Redirect to the Cancel Page
				model.addAttribute("account", account); // Add account details to the model
				return "redirect:/Account/cancelPage"; // Redirect to the Cancel Page
			}
	
			return "redirect:/Account"; // If account not found, redirect to the account list
		}
	
		// Method to validate OTP
		private boolean validateOTP(String otp, HttpSession session) {
			// Retrieve the OTP from the session
			String generatedOTP = (String) session.getAttribute("generatedOTP");
	
			// Validate if the OTP entered by the user matches the one stored in the session
			return generatedOTP != null && generatedOTP.equals(otp);
		}
	
		// Method to verify the OTP and proceed with account cancellation
		@PostMapping("/Account/otp/verify")
		public String verifyOTP(@RequestParam("otp") String otp, Model model, HttpSession session) {
			// Validate the OTP
			boolean isValidOTP = validateOTP(otp, session);
	
			if (isValidOTP) {
				// Proceed with account cancellation if OTP is valid
				Integer accountId = (Integer) session.getAttribute("accountId");
				if (accountId != null) {
					Account account = accountRepository.findById(accountId).orElse(null);
					if (account != null) {
						// Update account status and save
						account.setStatus(Account.AccountStatus.CANCELLED_PENDING); // Example enum for status
						accountRepository.save(account);
						model.addAttribute("message", "Your account cancellation request is now pending.");
						return "confirmationPage"; // Redirect to confirmation page
					}
				}
			} else {
				// Handle invalid OTP
				model.addAttribute("otpError", "Invalid OTP. Please try again.");
				model.addAttribute("isOtpVisible", true); // Keep OTP form visible
				return "cancelAccountPage"; // Return to the cancel account page with error
			}
			return "redirect:/Account"; // Default redirect if something unexpected happens
		}
	
		// Utility method to generate OTP
		private String generateOTP() {
			Random rand = new Random();
			int otp = 100000 + rand.nextInt(900000); // Generate a 6-digit OTP
			return String.valueOf(otp);
		}
	
		/**
		 * View the balance of an Account.
		 * 
		 * @param id    ID of the account to view the balance
		 * @param model Thymeleaf model to add attributes for the view
		 * @return Thymeleaf template for viewing the account balance
		 */
		@GetMapping("/Account/Balance/View/{id}")
		public String viewAccountBalance(@PathVariable("id") Integer id, Model model, Principal principal) {
		    // Get the currently logged-in username
		    String loggedInUsername = principal.getName();

		    // Find the account by ID
		    Account account = accountRepository.findById(id).orElse(null);
		    if (account == null) {
		        model.addAttribute("error", "Account not found."); // Error message
		        return "redirect:/Account"; // Redirect if account not found
		    }

		    // Check if the logged-in user is the owner of the account
		    if (!account.getMember().getUsername().equals(loggedInUsername)) {
		        model.addAttribute("error", "You do not have permission to view this account."); // Error message
		        return "redirect:/Account"; // Redirect if unauthorized
		    }

		    // Add account details to the model for authorized users
		    model.addAttribute("account", account);
		    model.addAttribute("balance", account.getBalance());
		    return "ViewAccountBalance"; // Thymeleaf template for viewing the account balance
		}


	
		@GetMapping("/Admin/Account")
		public String viewAllAccounts(Model model) {
			// Fetch all accounts in the system (admin can view all accounts)
			List<Account> listAccount = accountRepository.findAll();
	
			// Add the list of accounts to the model to be rendered in the admin's view
			model.addAttribute("listAccount", listAccount);
	
			return "AdminViewAccounts"; // Return the page for viewing all accounts by the admin
		}
	
		@PostMapping("/Admin/Account/Approve/{id}")
		public String approveAccount(@PathVariable("id") Integer accountId) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account != null && account.getStatus() == Account.AccountStatus.PENDING) {
				account.setStatus(Account.AccountStatus.ACTIVE); // Update status to ACTIVE
				accountRepository.save(account); // Save the updated account
			}
			return "redirect:/Admin/Account"; // Redirect to view the updated list
		}
	
		@PostMapping("/Admin/Account/Reject/{id}")
		public String rejectAccount(@PathVariable("id") Integer accountId) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account != null && account.getStatus() == Account.AccountStatus.PENDING) {
				account.setStatus(Account.AccountStatus.REJECTED);
				accountRepository.save(account); // Save the updated account
			}
			return "redirect:/Admin/Account"; // Redirect to view the updated list
		}
	
		@GetMapping("/Admin/Account/Edit/{id}")
		public String editAccount(@PathVariable("id") int id, Model model) {
			// Fetch the account using the ID
			Account account = accountRepository.findById(id).orElse(null);
			if (account == null) {
				return "redirect:/Admin/Account"; // Redirect if account not found
			}
	
			// Add the account and account types to the model
			List<Account_type> accountTypeList = accountTypeRepository.findAll();
			model.addAttribute("account", account);
			model.addAttribute("accountTypeList", accountTypeList);
	
			return "EditAccount"; // Return the edit account page
		}
	
		@PostMapping("/Admin/Account/Edit/{id}")
		public String saveUpdatedAccountAdmin(@PathVariable("id") Integer accountId, Account account, Model model) {
			Account existingAccount = accountRepository.findById(accountId).orElse(null);
	
			if (existingAccount == null) {
				model.addAttribute("error", "Account not found.");
				return "redirect:/Admin/Account"; // Redirect if account not found
			}
	
			existingAccount.setAccountName(account.getAccountName());
			existingAccount.setAccountNumber(account.getAccountNumber());
			existingAccount.setBalance(account.getBalance());
			existingAccount.setBankName(account.getBankName());
			existingAccount.setHolderName(account.getHolderName());
			existingAccount.setAccountType(account.getAccountType());
	
			accountRepository.save(existingAccount);
	
			return "redirect:/Admin/Account";
		}
	
		@PostMapping("/Admin/Account/Delete/{id}")
		public String deleteAccount(@PathVariable("id") Integer id) {
			accountRepository.deleteById(id);
	
			return "redirect:/Admin/Account";
		}
	
		@PostMapping("/Admin/Account/Cancel/Approve/{id}")
		public String approveAccountCancellation(@PathVariable("id") Integer accountId) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account != null && account.getStatus() == Account.AccountStatus.CANCELLED_PENDING) {
				account.setStatus(Account.AccountStatus.CANCELLED); // Update status to CANCELLED
				accountRepository.save(account); // Save the updated account
			}
			return "redirect:/Admin/Account"; // Redirect to view the updated list
		}
	
		@PostMapping("/Admin/Account/Cancel/Reject/{id}")
		public String rejectAccountCancellation(@PathVariable("id") Integer accountId) {
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account != null && account.getStatus() == Account.AccountStatus.CANCELLED_PENDING) {
				account.setStatus(Account.AccountStatus.ACTIVE); // Revert status back to ACTIVE
				accountRepository.save(account); // Save the updated account
			}
			return "redirect:/Admin/Account"; // Redirect to view the updated list
		}
	
		// Method to update account balance after successful PayPal payment
		@PostMapping("/updateBalance")
		public String updateBalance(@RequestParam("transactionId") String transactionId,
				@RequestParam("amount") double amount, @RequestParam("accountId") int accountId, Model model) {
	
			// Fetch the account by account ID
			Account account = accountRepository.findById(accountId).orElse(null);
			if (account == null) {
				model.addAttribute("error", "Account not found.");
				return "redirect:/Account"; // Redirect if account not found
			}
	
			// Update the account balance
			account.setBalance(account.getBalance() + amount);
	
			// Save the updated account balance
			accountRepository.save(account);
	
			// Add the updated account to the model for display
			model.addAttribute("account", account);
			model.addAttribute("balance", account.getBalance());
	
			// Optionally log the transaction details
			System.out.println("Transaction ID: " + transactionId);
			System.out.println("Amount added: $" + amount);
			System.out.println("Updated Account Balance: $" + account.getBalance());
	
			return "redirect:/Account"; // Redirect to Account page
		}
		@GetMapping("/Admin/Account/Export")
		public ResponseEntity<Void> exportAccountsToCsv(HttpServletResponse response) {
		    try {
		        // Set response headers
		        response.setContentType("text/csv");
		        response.setHeader("Content-Disposition", "attachment; filename=accounts.csv");

		        // Retrieve account data (replace with your service/repository logic)
		        List<Account> accounts = accountRepository.findAll(); // Replace with accountService logic if applicable
		        int accountCount = accounts.size(); // Count the number of accounts

		        // Write CSV data
		        PrintWriter writer = response.getWriter();
		        writer.println("Account ID,Account Holder,Account Type,Balance,Status");

		        for (Account account : accounts) {
		            writer.printf("%d,%s,%s,%.2f,%s%n",
		                account.getId(),
		                account.getHolderName(),
		                account.getAccountType().getName(),
		                account.getBalance(),
		                account.getStatus());
		        }

		        // Add a summary row with the count
		        writer.println();
		        writer.printf("Total Accounts:,%d%n", accountCount); // Summary row

		        writer.flush();
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.internalServerError().build();
		    }

		    return ResponseEntity.ok().build();
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
	
