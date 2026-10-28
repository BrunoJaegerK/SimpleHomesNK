package com.brunojaegerk.simplehomes.homes;

import cn.nukkit.Player;

import com.brunojaegerk.simplehomes.DatabaseManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private final DatabaseManager databaseManager;
    private final Map<String, Object> homeLimits;
    private final int defaultLimit;
    private final boolean opUnlimited;

    public HomeManager(
            DatabaseManager databaseManager,
            Map<String, Object> homeLimits,
            int defaultLimit,
            boolean opUnlimited
    ) {
        if (databaseManager == null) {
            throw new IllegalArgumentException(
                    "DatabaseManager no puede ser null."
            );
        }

        this.databaseManager = databaseManager;
        this.homeLimits = homeLimits;
        this.defaultLimit = Math.max(0, defaultLimit);
        this.opUnlimited = opUnlimited;
    }

    public int getHomeLimit(Player player) {
        if (player == null) {
            return defaultLimit;
        }

        if (opUnlimited && player.isOp()) {
            return Integer.MAX_VALUE;
        }

        int limit = defaultLimit;

        for (Map.Entry<String, Object> entry : homeLimits.entrySet()) {
            String permission = entry.getKey();
            Object value = entry.getValue();

            if (!(value instanceof Number)) {
                continue;
            }

            int configuredLimit = ((Number) value).intValue();

            if (configuredLimit >= 0
                    && configuredLimit > limit
                    && player.hasPermission(permission)) {
                limit = configuredLimit;
            }
        }

        return limit;
    }

    public int getHomeCount(UUID playerUUID) throws SQLException {
        return getHomes(playerUUID).size();
    }

    public boolean canCreateHome(Player player) throws SQLException {
        if (player == null) {
            return false;
        }

        int limit = getHomeLimit(player);

        return limit == Integer.MAX_VALUE
                || getHomeCount(player.getUniqueId()) < limit;
    }

    public boolean setHome(
            UUID playerUUID,
            String homeName,
            String worldName,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) throws SQLException {

        validateHomeName(homeName);

        if (playerUUID == null) {
            throw new IllegalArgumentException(
                    "El UUID del jugador no puede ser null."
            );
        }

        if (worldName == null || worldName.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del mundo no puede estar vacío."
            );
        }

        Home home = new Home(
                playerUUID,
                homeName,
                worldName,
                x,
                y,
                z,
                yaw,
                pitch,
                System.currentTimeMillis()
        );

        return databaseManager.createHome(home);
    }

    public Home getHome(UUID playerUUID, String homeName)
            throws SQLException {

        validatePlayer(playerUUID);
        validateHomeName(homeName);

        return databaseManager.getHome(playerUUID, homeName);
    }

    public List<Home> getHomes(UUID playerUUID) throws SQLException {
        validatePlayer(playerUUID);

        return databaseManager.getHomes(playerUUID);
    }

    public boolean deleteHome(UUID playerUUID, String homeName)
            throws SQLException {

        validatePlayer(playerUUID);
        validateHomeName(homeName);

        return databaseManager.deleteHome(playerUUID, homeName);
    }

    public boolean renameHome(
            UUID playerUUID,
            String oldName,
            String newName
    ) throws SQLException {

        validatePlayer(playerUUID);
        validateHomeName(oldName);
        validateHomeName(newName);

        if (oldName.equals(newName)) {
            return false;
        }

        if (!oldName.equalsIgnoreCase(newName)
                && databaseManager.homeExists(playerUUID, newName)) {
            throw new IllegalArgumentException(
                    "Ya tienes un hogar con ese nombre."
            );
        }

        return databaseManager.renameHome(
                playerUUID,
                oldName,
                newName
        );
    }

    public boolean homeExists(UUID playerUUID, String homeName)
            throws SQLException {

        validatePlayer(playerUUID);
        validateHomeName(homeName);

        return databaseManager.homeExists(playerUUID, homeName);
    }

    private void validatePlayer(UUID playerUUID) {
        if (playerUUID == null) {
            throw new IllegalArgumentException(
                    "El UUID del jugador no puede ser null."
            );
        }
    }

    private void validateHomeName(String homeName) {
        if (homeName == null || homeName.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del hogar no puede estar vacío."
            );
        }

        if (homeName.length() > 32) {
            throw new IllegalArgumentException(
                    "El nombre del hogar no puede superar 32 caracteres."
            );
        }

        if (!homeName.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "El nombre solo puede contener letras, números, "
                            + "guiones y guiones bajos."
            );
        }
    }
}


