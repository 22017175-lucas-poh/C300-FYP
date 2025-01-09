/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2025-Jan-09 5:37:59 am 
 * 
 */

package E63C.Lucas.LP01;

/**
 * @author Lenovo
 *
 */
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // Find CartItems by the memberId (or other attributes you might need)
    List<CartItem> findByMemberId(int memberId);
}