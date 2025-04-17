package com.github.groundbreakingmc.simplecrates.database;

import com.github.groundbreakingmc.mylib.database.Database;
import com.github.groundbreakingmc.mylib.database.DatabaseUtils;
import com.github.groundbreakingmc.simplecrates.SimpleCrates;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public final class DatabaseManager extends Database {

    @SuppressWarnings("unused")
    public static final String SET_PLAYER_KEYS = """
            INSERT INTO player_keys (player_uuid, case_type, key_count)
            VALUES (?, ?, ?)
            ON CONFLICT(player_uuid, case_type)
            DO UPDATE SET key_count = excluded.key_count;""";

    public static final String ADD_PLAYER_KEYS = """
            INSERT INTO player_keys (player_uuid, case_type, key_count)
            VALUES (?, ?, ?)
            ON CONFLICT(player_uuid, case_type)
            DO UPDATE SET key_count = key_count + excluded.key_count;""";

    public static final String REMOVE_PLAYER_KEYS = """
            INSERT INTO player_keys (player_uuid, case_type, key_count)
            VALUES (?, ?, ?)
            ON CONFLICT(player_uuid, case_type)
            DO UPDATE SET key_count = key_count - excluded.key_count;""";

    public static final String GET_PLAYER_KEYS = """
            SELECT case_type, key_count
            FROM player_keys
            WHERE player_uuid = ?;""";

    public DatabaseManager(final SimpleCrates plugin) {
        super(DatabaseUtils.getSQLiteDriverUrl(plugin));
        this.createTable();
    }

    private void createTable() {
        final String query = """
                CREATE TABLE IF NOT EXISTS player_keys (
                    player_uuid TEXT NOT NULL,
                    case_type TEXT NOT NULL DEFAULT 0,
                    key_count INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (player_uuid, case_type)
                );""";
        try (final Connection connection = super.getConnection()) {
            super.createTables(connection, query);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void updatePlayerKeys(final String query, final UUID playerUUID,
                                 final String caseType, final int keyCount) {
        try (final Connection connection = super.getConnection()) {
            super.executeUpdateQuery(query, connection, playerUUID, caseType, keyCount);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Map<String, Integer> getPlayerKeys(final UUID playerUUID) {
        final Map<String, Integer> keysMap = new Object2ObjectOpenHashMap<>();
        try (final Connection connection = super.getConnection();
             final ResultSet resultSet = super.getStatement(
                     GET_PLAYER_KEYS,
                     connection,
                     playerUUID.toString()).executeQuery()) {
            while (resultSet.next()) {
                String caseType = resultSet.getString("case_type");
                int keyCount = resultSet.getInt("key_count");
                keysMap.put(caseType, keyCount);
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return keysMap;
    }
}
