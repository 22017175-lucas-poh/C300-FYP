/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Jan-12 3:27:47 pm 
 * 
 */
package E63C.Lucas.LP01;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class GetUsername implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private static final Logger LOG = Logger.getLogger(GetUsername.class.getName());

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        LOG.info("Failed login using USERNAME [" + username + "]");
        
        // Update failed attempts count in the database and handle account locking
        updateFailedAttempts(username);
    }

    private void updateFailedAttempts(String username) {
        Member member = memberRepository.findByUsername(username);

        if (member != null) {
            int currentFailedAttempts = member.getFailedAttempt();
            currentFailedAttempts++;

            if (currentFailedAttempts >= 5) {
                // Lock the account
                member.setAccountNonLocked(false);
            }

            member.setFailedAttempt(currentFailedAttempts);

            // Reset failed attempts count to 0 if login is successful
            if (member.isAccountNonLocked() && member.getFailedAttempt() == 0) {
                member.setAccountNonLocked(true);
            }

            memberRepository.save(member);
        }
    }
}