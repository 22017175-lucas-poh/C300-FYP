/**
 * 
 * I declare that this code was written by me, lucas. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-04 5:37:58 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Lenovo
 *
 */

public interface AccountRepository extends JpaRepository<Account_type,Integer>{
    List<Account_type> findByName(String name);

}
