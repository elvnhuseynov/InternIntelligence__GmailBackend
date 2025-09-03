package az.gmail.repository;

import az.gmail.entity.EmailStar;
import az.gmail.entity.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailStarRepository extends JpaRepository<EmailStar, Long> {
    boolean existsByEmail_IdAndUser_Id(Long emailId, Long userId);
    void deleteByEmail_IdAndUser_Id(Long emailId, Long userId);

    @Query("SELECT s.email FROM EmailStar s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    Page<Email> findStarredEmailsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM EmailStar s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
