package com.daniel.mangavault.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Removes story/genre links from both the current join table and the legacy one.
 * Some existing installations contain data in both tables after the table was
 * renamed, so cleaning only the table mapped by Hibernate leaves a blocking FK.
 */
@Repository
@RequiredArgsConstructor
public class StoryRelationCleanupRepository {
    private static final Set<String> SUPPORTED_TABLES = Set.of("story_genres", "story_categories");

    private final JdbcTemplate jdbcTemplate;

    public void deleteGenreLinks(String storyId) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            DatabaseMetaData metadata = connection.getMetaData();
            for (String table : SUPPORTED_TABLES) {
                if (tableExists(metadata, connection.getCatalog(), table)) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "delete from " + table + " where story_id = ?")) {
                        statement.setString(1, storyId);
                        statement.executeUpdate();
                    }
                }
            }
            return null;
        });
    }

    private boolean tableExists(DatabaseMetaData metadata, String catalog, String expectedName) throws SQLException {
        try (ResultSet tables = metadata.getTables(catalog, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                if (expectedName.equalsIgnoreCase(tables.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
