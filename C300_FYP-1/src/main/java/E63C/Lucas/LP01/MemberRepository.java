
package E63C.Lucas.LP01;


import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Integer> {

    // Find Member by Account ID
	Member findByAccountId(int accountId);
}

