package az.gmail.repository;

import az.gmail.entity.EmailRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRecipientRepository extends JpaRepository<EmailRecipient, Long>{
}
