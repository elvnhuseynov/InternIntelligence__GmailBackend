package az.gmail.repository;

import az.gmail.entity.EmailLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailLabelRepository extends JpaRepository<EmailLabel, Long> {
    Optional<EmailLabel> findByCode(String code);
}
