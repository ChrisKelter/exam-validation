package at.oeh.uni.innsbruck.stadtrad.examValidation.repository;

import at.oeh.uni.innsbruck.stadtrad.examValidation.model.Validation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ValidationRepository extends JpaRepository<Validation, String> {
    boolean existsByStudentId(String string);
    Validation findByStudentId(String string);
    Page<Validation> findAll(Pageable pageable);

    @Query("""
        SELECT v FROM Validation v
        WHERE 
            (:inputText IS NULL OR LOWER(v.email) LIKE LOWER(CONCAT('%', :inputText, '%')))
            OR (:inputText IS NULL OR STR(v.studentId) LIKE CONCAT('%', :inputText, '%'))
    """)
    Page<Validation> findAllByFilters(
            @Param("inputText") String inputText,
            Pageable pageable
    );

    boolean existsByStudentIdAndEmailAndValidUntilAfter(String studentId, String email, Date validUntil);
}
