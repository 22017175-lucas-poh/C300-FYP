/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Jan-12 11:39:05 am 
 * 
 */

package E63C.Lucas.LP01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author lucas
 *
 */
public class MemberDetailsService implements UserDetailsService{
	@Autowired
	private MemberRepository memberRepository;
	
	@Override
	public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		Member member =memberRepository.getByUsername(username);
		
		if(member == null) {
			throw new UsernameNotFoundException("Could not find user");
		}
		return new MemberDetails(member);
		}
	}
	


