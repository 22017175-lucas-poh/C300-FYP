/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-04 5:37:58 pm 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    // Update the method to use accountName instead of name
    List<Account> findByAccountName(String accountName);  
    List<Account> findByMemberId(Integer memberId);
    
    Account findFirstByAccountNumber(String accountNumber);
    
}
