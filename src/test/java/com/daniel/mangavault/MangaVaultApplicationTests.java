package com.daniel.mangavault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the whole application context against H2.
 * <p>
 * This is broader than it looks: Spring Data parses and validates every
 * {@code @Query} while creating repository beans, and Hibernate builds the schema
 * from the entity mappings. A malformed JPQL query or a broken association fails
 * here rather than at runtime.
 */
@SpringBootTest
@ActiveProfiles("test")
class MangaVaultApplicationTests {

	@Test
	void contextLoads() {
	}

}
