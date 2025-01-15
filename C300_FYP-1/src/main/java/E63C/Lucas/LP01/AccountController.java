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

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

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
	public String saveAccount(Account account, Model model) {
		// Get the currently logged-in user's ID
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String loggedInUsername = authentication.getName(); // Assuming username is unique
		Integer loggedInUserId = getCurrentUserId(loggedInUsername);

		// Fetch the Member entity using the logged-in user's ID
		Member member = memberRepository.findById(loggedInUserId).orElse(null);
		if (member == null) {
			model.addAttribute("error", "Member not found.");
			return "redirect:/Account"; // Handle the error properly
		}

		// Set default values
		account.setStatus(Account.AccountStatus.PENDING); // Default status
		account.setAccountNumber(generateRandomAccountNumber()); // Generate random account number
		account.setCreationDate(Date.valueOf(LocalDate.now())); // Set today's date as java.sql.Date
		account.setBalance(0.0); // Default balance
		account.setBankName("RP Digital Bank");
		account.setMember(member); // Associate account with the logged-in user (member)

		accountRepository.save(account); // Save the account
		return "redirect:/Account"; // Redirect to the account list page
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

	/**
	 * Delete an Account by ID.
	 * 
	 * @param id ID of the account to delete
	 * @return Redirect to the accounts list
	 */
	@PostMapping("/Account/cancel/{id}")
	public String cancelAccount(@PathVariable("id") Integer id) {
		// Check if the account exists
		if (accountRepository.existsById(id)) {
			Account account = accountRepository.findById(id).orElse(null);
			if (account != null) {
				account.setStatus(Account.AccountStatus.CANCELLED_PENDING); // Update the status
				accountRepository.save(account); // Save the updated account
			}
		}
		return "redirect:/Account"; // Redirect to the accounts list
	}

	/**
	 * View the balance of an Account.
	 * 
	 * @param id    ID of the account to view the balance
	 * @param model Thymeleaf model to add attributes for the view
	 * @return Thymeleaf template for viewing the account balance
	 */
	@GetMapping("/Account/Balance/View/{id}")
	public String viewAccountBalance(@PathVariable("id") Integer id, Model model) {
		Account account = accountRepository.findById(id).orElse(null); // Find account by ID
		if (account == null) {
			model.addAttribute("error", "Account not found."); // Error message
			return "redirect:/Account"; // Redirect if account not found
		}

		model.addAttribute("account", account); // Add account to the model for viewing balance
		model.addAttribute("balance", account.getBalance()); // Add balance to the model
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
}
