package com.example.taskmanager.user;

import com.example.taskmanager.common.Role;
import com.example.taskmanager.common.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    /** Used when updating a user, so the user's own email does not count as a conflict. */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Lists users with optional filters. A null argument means "don't filter by this".
     * Search matches the name or username (case-insensitive, partial).
     */
    @Query("""
            select u from User u
            where (:search is null
                   or lower(u.name) like lower(concat('%', :search, '%'))
                   or lower(u.username) like lower(concat('%', :search, '%')))
              and (:role is null or u.role = :role)
              and (:status is null or u.status = :status)
            order by u.name
            """)
    List<User> search(@Param("search") String search,
                      @Param("role") Role role,
                      @Param("status") UserStatus status);
}
