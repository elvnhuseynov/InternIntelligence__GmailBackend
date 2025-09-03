package az.gmail.repository;

import az.gmail.entity.EmailLabelMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLabelMapRepository extends JpaRepository<EmailLabelMap, Long> {
    @Query("SELECT COUNT(m) FROM EmailLabelMap m WHERE m.user.id = :userId AND m.label.code = :code")
    long countByUserIdAndLabelCode(@Param("userId") Long userId, @Param("code") String code);
}
