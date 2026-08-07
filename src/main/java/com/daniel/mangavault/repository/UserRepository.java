package com.daniel.mangavault.repository;

import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.Role;
import com.daniel.mangavault.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    /** Admin console search across username, email and display name. */
    @Query("""
            select u from User u
            where (:keyword = '' or lower(u.username) like lower(concat('%', :keyword, '%'))
                                 or lower(u.email) like lower(concat('%', :keyword, '%'))
                                 or lower(u.displayName) like lower(concat('%', :keyword, '%')))
              and (:role is null or u.role = :role)
              and (:status is null or u.status = :status)
            """)
    Page<User> search(@Param("keyword") String keyword,
                      @Param("role") Role role,
                      @Param("status") UserStatus status,
                      Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);
}
