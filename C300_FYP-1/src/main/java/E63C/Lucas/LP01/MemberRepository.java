
package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	List<Member> findByName(String name);

	public Member findByUsername(String username);

	@Query("UPDATE Member u SET u.failedAttempt = ?1 WHERE u.username = ?2")
	@Modifying
	public void updateFailedAttempts(int failAttempts, String username);

	Member getByUsername(String username);

	Member accountNonLocked(boolean accountNonLocked);
}
