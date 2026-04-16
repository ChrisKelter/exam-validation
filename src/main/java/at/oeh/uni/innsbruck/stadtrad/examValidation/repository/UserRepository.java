package at.oeh.uni.innsbruck.stadtrad.examValidation.repository;

import at.oeh.uni.innsbruck.stadtrad.examValidation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
