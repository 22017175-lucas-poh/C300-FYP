/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2025-Jan-20 8:12:44 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lenovo
 *
 */
@Service
public class AccountTypeService {

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    /**
     * Updates interest rates based on account type every 30 days.
     */
    @Transactional
    public void updateInterestRates() {
        List<Account_type> accountTypes = accountTypeRepository.findAll();

        for (Account_type accountType : accountTypes) {
            if (accountType.getId() == 1) {
                // Increment interest rate for type 1
                accountType.setInterestRate(accountType.getInterestRate() + 0.05);
            } else if (accountType.getId() == 2) {
                // Increment interest rate for type 2
                accountType.setInterestRate(accountType.getInterestRate() + 0.08);
            }
            // Save the updated account type
            accountTypeRepository.save(accountType);
        }
    }
}
