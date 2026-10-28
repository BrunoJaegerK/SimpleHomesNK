package com.brunojaegerk.simplehomes;

import com.brunojaegerk.simplehomes.homes.Home;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final File databaseFile;
    private Connection connection;

    public DatabaseManager(File dataFolder) {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException(
                    "No se pudo crear la carpeta de datos de SimpleHomes."
            );
        }

        this.databaseFile = new File(dataFolder, "simplehomes.db");
    }

    public synchronized void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "No se encontró el controlador SQLite.",
                    exception
            );
        }

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile.getAbsolutePath()
        );

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS homes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uuid TEXT NOT NULL,
                        home_name TEXT NOT NULL COLLATE NOCASE,
                        world_name TEXT NOT NULL,
                        x REAL NOT NULL,
                        y REAL NOT NULL,
                        z REAL NOT NULL,
                        yaw REAL NOT NULL,
                        pitch REAL NOT NULL,
                        created_at INTEGER NOT NULL,
                        UNIQUE(player_uuid, home_name)
                    )
                    """);

            statement.execute("""
                    CREATE INDEX IF NOT EXISTS idx_homes_player
                    ON homes(player_uuid)
                    """);
        } catch (SQLException exception) {
            close();
            throw exception;
        }
    }

    public synchronized boolean createHome(Home home)
            throws SQLException {

        String sql = """
                INSERT OR IGNORE INTO homes (
                    player_uuid, home_name, world_name,
                    x, y, z, yaw, pitch, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, home.getPlayerUUID().toString());
            statement.setString(2, home.getName());
            statement.setString(3, home.getWorldName());
            statement.setDouble(4, home.getX());
            statement.setDouble(5, home.getY());
            statement.setDouble(6, home.getZ());
            statement.setFloat(7, home.getYaw());
            statement.setFloat(8, home.getPitch());
            statement.setLong(9, home.getCreatedAt());

            return statement.executeUpdate() > 0;
        }
    }

    public synchronized Home getHome(UUID playerUUID, String homeName)
            throws SQLException {

        String sql = """
                SELECT player_uuid, home_name, world_name,
                       x, y, z, yaw, pitch, created_at
                FROM homes
                WHERE player_uuid = ? AND home_name = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, playerUUID.toString());
            statement.setString(2, homeName);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return readHome(result);
            }
        }
    }

    public synchronized List<Home> getHomes(UUID playerUUID)
            throws SQLException {

        List<Home> homes = new ArrayList<>();

        String sql = """
                SELECT player_uuid, home_name, world_name,
                       x, y, z, yaw, pitch, created_at
                FROM homes
                WHERE player_uuid = ?
                ORDER BY home_name COLLATE NOCASE
                """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, playerUUID.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    homes.add(readHome(result));
                }
            }
        }

        return homes;
    }

    public synchronized boolean deleteHome(UUID playerUUID, String homeName)
            throws SQLException {

        String sql = """
                DELETE FROM homes
                WHERE player_uuid = ? AND home_name = ?
                """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, playerUUID.toString());
            statement.setString(2, homeName);

            return statement.executeUpdate() > 0;
        }
    }


    public synchronized boolean renameHome(
            UUID playerUUID,
            String oldName,
            String newName
    ) throws SQLException {

        String sql = """
            UPDATE homes
            SET home_name = ?
            WHERE player_uuid = ? AND home_name = ?
            """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, newName);
            statement.setString(2, playerUUID.toString());
            statement.setString(3, oldName);

            return statement.executeUpdate() > 0;
        }
    }

    public synchronized boolean homeExists(UUID playerUUID, String homeName)
            throws SQLException {

        String sql = """
                SELECT 1 FROM homes
                WHERE player_uuid = ? AND home_name = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     getConnectionChecked().prepareStatement(sql)) {

            statement.setString(1, playerUUID.toString());
            statement.setString(2, homeName);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private Home readHome(ResultSet result) throws SQLException {
        return new Home(
                UUID.fromString(result.getString("player_uuid")),
                result.getString("home_name"),
                result.getString("world_name"),
                result.getDouble("x"),
                result.getDouble("y"),
                result.getDouble("z"),
                result.getFloat("yaw"),
                result.getFloat("pitch"),
                result.getLong("created_at")
        );
    }

    private Connection getConnectionChecked() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException(
                    "La base de datos de SimpleHomes no está conectada."
            );
        }

        return connection;
    }

    public synchronized Connection getConnection() {
        return connection;
    }

    public synchronized void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException exception) {
            System.err.println(
                    "[SimpleHomes] Error al cerrar SQLite: "
                            + exception.getMessage()
            );
        } finally {
            connection = null;
        }
    }
}



