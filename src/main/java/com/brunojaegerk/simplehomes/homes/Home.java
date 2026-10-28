package com.brunojaegerk.simplehomes.homes;

import java.util.UUID;

public class Home {

    private final UUID playerUUID;
    private final String name;
    private final String worldName;

    private final double x;
    private final double y;
    private final double z;

    private final float yaw;
    private final float pitch;

    private final long createdAt;

    public Home(
            UUID playerUUID,
            String name,
            String worldName,
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            long createdAt
    ) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.createdAt = createdAt;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}

