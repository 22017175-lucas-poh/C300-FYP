/**
 * 
 * I declare that this code was written by me, 22024937. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Royce
 * Student ID: 22024937
 * Class: E63C
 * Date created: 2025-Jan-15 3:13:26 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;

/**
 * @author 22024937
 *
 */


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface ConsultantRepository extends JpaRepository<Consultant, Integer> {
    List<Consultant> findAll();
}

