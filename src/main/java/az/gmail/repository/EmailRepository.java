package az.gmail.repository;

import az.gmail.entity.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    @Query("SELECT m.email FROM EmailLabelMap m WHERE m.user.id = :userId AND m.label.code = " +
           ":code ORDER BY m.email.createdAt DESC")
    Page<Email> findEmailsByUserIdAndLabelCode(@Param("userId") Long userId, @Param("code") String code,
                                               Pageable pageable);
}
