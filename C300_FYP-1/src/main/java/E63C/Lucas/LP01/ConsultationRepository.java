package E63C.Lucas.LP01;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer>{
	List<Consultation> findbyNumber (int consultationName);


}
