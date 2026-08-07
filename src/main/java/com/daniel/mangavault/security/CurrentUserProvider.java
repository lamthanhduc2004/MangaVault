package com.daniel.mangavault.security;

import com.daniel.mangavault.entity.User;
import com.daniel.mangavault.enums.UserStatus;
import com.daniel.mangavault.exception.AppException;
import com.daniel.mangavault.exception.ErrorCode;
import com.daniel.mangavault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Resolves the authenticated user from the JWT.
 * <p>
 * Tokens issued by {@code AuthService} already carry a {@code uid} claim, so the
 * user id is available without a database round-trip and without a custom
 * {@code UserDetailsService}.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final UserRepository userRepository;

    /** User id from the token. Throws when the request is anonymous. */
    public String requireUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = jwt.getClaimAsString("uid");
        if (userId == null || userId.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userId;
    }

    /**
     * Loads the user entity and rejects locked accounts. JWTs are stateless, so a
     * token issued before a ban stays cryptographically valid — the ban is enforced
     * here, on every request that acts on the user's behalf.
     */
    public User requireUser() {
        User user = userRepository.findById(requireUserId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        if (user.getStatus() == UserStatus.BANNED) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }
        return user;
    }

    /** Reference proxy for writes that only need the foreign key — avoids a SELECT. */
    public User referenceToCurrentUser() {
        return userRepository.getReferenceById(requireUserId());
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
