
package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
	List<Member> findByName(String name);

	public Member findByUsername(String username);

	@Query("UPDATE Member u SET u.failedAttempt = ?1 WHERE u.username = ?2")
	@Modifying
	public void updateFailedAttempts(int failAttempts, String username);

	@Query("SELECT MAX(m.customId) FROM Member m WHERE m.customId LIKE CONCAT(:prefix, '%')")
	String findMaxCustomIdByPrefix(@Param("prefix") String prefix);
	Member findById(int id);
	Member getByUsername(String username);
	Member accountNonLocked(boolean accountNonLocked);
	Member findByEmail(String email);
	Member findByNric(String nric);
}
