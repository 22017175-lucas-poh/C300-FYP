/**
 * 
 * I declare that this code was written by me, Lenovo. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Lucas poh zhan le
 * Student ID: 22017175
 * Class: E63C
 * Date created: 2024-Nov-06 7:34:52 pm 
 * 
 */

package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Lenovo
 *
 */
public interface CardRepository extends JpaRepository<Card, Integer> {

    @Query("SELECT c FROM Card c WHERE c.Cardnumber = :cardnumber")
    List<Card> findByCardnumber(@Param("cardnumber") int cardnumber);
}

