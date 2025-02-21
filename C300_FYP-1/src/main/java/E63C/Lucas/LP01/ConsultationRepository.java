package E63C.Lucas.LP01;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {
    List<Consultation> findByMember(Member member);

   
    
    List<Consultation> findByConsultantNameAndConsultationDateAndConsultationTime(String consultantName, Date consultationDate, String consultationTime);
    
    List<Consultation> findByMemberAndConsultationDateAndConsultationTime(Member member, Date consultationDate, String consultationTime);

    boolean existsByMemberAndConsultationDateAndConsultationTime(Member member, Date consultationDate, String consultationTime);

    Collection<Consultation> findByConsultationDate(Date consultationDate);

    // New function to count total consultations on a given date
    int countByConsultationDate(Date consultationDate);
}
