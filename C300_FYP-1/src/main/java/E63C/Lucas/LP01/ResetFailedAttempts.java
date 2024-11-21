/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Feb-02 10:50:32 am 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * @author lucas
 *
 */
@Component
public class ResetFailedAttempts implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger LOG = Logger.getLogger(ResetFailedAttempts.class.getName());

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
    	 MemberDetails userDetails = (MemberDetails) event.getAuthentication().getPrincipal();
    	    String username = userDetails.getUsername();
    	    LOG.info("Successful login using USERNAME [" + username + "]");

        // Reset failed attempts count to 0 on successful login
        resetFailedAttempts(username);
    }

    private void resetFailedAttempts(String username) {
        Member member = memberRepository.findByUsername(username);

        if (member != null) {
            // Reset failed attempts count to 0 on successful login
            member.setFailedAttempt(0);

            memberRepository.save(member);
        }
    }
}