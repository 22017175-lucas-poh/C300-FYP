package E63C.Lucas.LP01;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
	public MemberDetailsService memberDetailsService() {
		return new MemberDetailsService();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(memberDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
	            // BO and FA access
	            .requestMatchers("/Admin/Card", "/Admin/Card/Approve/*", "/Admin/Card/Reject/*", 
	                             "/Admin/Card/Edit/*", "/Admin/Card/Delete/*", 
	                             "/Account_type", "/Account_type/add", "/Account_type/edit/*", "/Account_type/delete/*")
	            .hasAnyRole("BO", "FA")
	            .requestMatchers("/Admin/consultations", "/Admin/consultations/edit/*", "/Admin/consultations/delete")
	            .hasRole("FA")
	            .requestMatchers("/Member", "/Member/add", "/Member/edit/*", "/Member/delete/*")
	            .hasRole("BO")
	            .requestMatchers("/").permitAll() // Home page is visible without logging in
	            .requestMatchers("/register").permitAll()
	            .requestMatchers("/forget").permitAll()
	            .requestMatchers("/bootstrap/*/*", "/images/*").permitAll() // Static resources
	            .anyRequest().hasRole("ADMIN"))
	            .formLogin((login) -> login.loginPage("/login").permitAll().defaultSuccessUrl("/")) // Goes to homepage upon login
	            .logout((logout) -> logout.logoutSuccessUrl("/")) // Goes to homepage upon logout
	            .exceptionHandling((exceptionHandling) -> exceptionHandling.accessDeniedPage("/403"));

	    return http.build();
	}

}
