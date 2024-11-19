package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, String> {
	List<Consultation> findById(int id);

	//List<Consultation> findAll();

	/**
	 * @param consultation
	 */
	//void save(Consultation consultation);
}
