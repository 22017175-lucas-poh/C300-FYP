package E63C.Lucas.LP01;
/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2025-Jan-20 7:53:08 pm 
 * 
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Lenovo
 *
 */
@Component
public class InterestRateScheduler {

    @Autowired
    private AccountTypeService accountTypeService;

    /**
     * Scheduled task to update interest rates every 30 days.
     * The cron expression runs the task every 30 days at midnight.
     */
    @Scheduled(cron = "0 0 0 1/30 * ?")
    public void scheduleInterestRateUpdate() {
        accountTypeService.updateInterestRates();
    }
}

