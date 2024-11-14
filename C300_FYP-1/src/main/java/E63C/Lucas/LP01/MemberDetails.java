	/**
	 * 
	 * I declare that this code was written by me, lucas. 
	 * I will not copy or allow others to copy my code. 
	 * I understand that copying code is considered as plagiarism.
	 * 
	 * Student Name: Lucas poh zhan le
	 * Student ID: 22017175
	 * Class: E63C
	 * Date created: 2024-Jan-12 11:15:56 am 
	 * 
	 */
	
	package E63C.Lucas.LP01;
	
	import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
	
	/**
	 * @author lucas
	 *
	 */
	public class MemberDetails  implements UserDetails{
		private Member member;
	
		/**
		 * @param member
		 */
		public MemberDetails(Member member) {
			this.member = member;
		}
	
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities(){
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole()); 
			return Arrays.asList(authority);
	
		}
	
		@Override
		public String getPassword() {
			return member.getPassword();
		}
	
		@Override
		public String getUsername() {
			return member.getUsername();
		}
		
		public int getFailedAttempt() {
			return member.getFailedAttempt();
		}
		public String getNric() {
			return member.getNric();
		}
	
		@Override
		public boolean isAccountNonExpired() {
			return true;
		}
	
		@Override
		public boolean isAccountNonLocked() {
	        return member.isAccountNonLocked();
		}
	
		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}
	
		@Override
		public boolean isEnabled() {
			return true;
		}

		public Member getMember() {
			return member;
		}

		public void setMember(Member member) {
			this.member = member;
		}
		
		
		
	}
	
	
	
