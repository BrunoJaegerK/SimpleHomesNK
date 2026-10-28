package com.brunojaegerk.simplehomes;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeTeleportManager implements Listener {

    private final SimpleHomes plugin;
    private final int delaySeconds;

    private final Map<UUID, PendingTeleport> pendingTeleports =
            new HashMap<>();

    public HomeTeleportManager(SimpleHomes plugin, int delaySeconds) {
        this.plugin = plugin;
        this.delaySeconds = Math.max(0, delaySeconds);
    }

    public void teleport(Player player, Location destination, String homeName) {
        if (player == null || destination == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (pendingTeleports.containsKey(uuid)) {
            player.sendMessage("§cYa tienes un teletransporte pendiente.");
            return;
        }

        if (destination.getLevel() == null) {
            player.sendMessage("§cEl mundo de destino no está disponible.");
            return;
        }

        if (delaySeconds <= 0) {
            completeTeleport(player, destination, homeName);
            return;
        }

        PendingTeleport pending = new PendingTeleport(
                destination,
                homeName,
                delaySeconds
        );

        pendingTeleports.put(uuid, pending);

        player.sendMessage(
                "§aTeletransporte en §e" + delaySeconds
                        + " §asegundos. No te muevas."
        );

        pending.taskHandler = plugin.getServer()
                .getScheduler()
                .scheduleRepeatingTask(plugin, new Task() {
                    @Override
                    public void onRun(int currentTick) {
                        PendingTeleport current =
                                pendingTeleports.get(uuid);

                        if (current != pending) {
                            getHandler().cancel();
                            return;
                        }

                        if (!player.isOnline()) {
                            pendingTeleports.remove(uuid);
                            pending.taskHandler = null;
                            getHandler().cancel();
                            return;
                        }

                        pending.remainingSeconds--;

                        if (pending.remainingSeconds <= 0) {
                            pendingTeleports.remove(uuid);
                            pending.taskHandler = null;
                            getHandler().cancel();

                            completeTeleport(
                                    player,
                                    pending.destination,
                                    pending.homeName
                            );
                        } else {
                            player.sendMessage(
                                    "§aTeletransporte en §e"
                                            + pending.remainingSeconds
                                            + " §asegundos..."
                            );
                        }
                    }
                }, 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!pendingTeleports.containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getTo() == null) {
            return;
        }

        double dx = event.getTo().getX() - event.getFrom().getX();
        double dy = event.getTo().getY() - event.getFrom().getY();
        double dz = event.getTo().getZ() - event.getFrom().getZ();

        if (Math.abs(dx) > 0.01
                || Math.abs(dy) > 0.01
                || Math.abs(dz) > 0.01) {
            cancelTeleport(
                    player,
                    "§cTeletransporte cancelado: te has movido."
            );
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (pendingTeleports.containsKey(player.getUniqueId())) {
            cancelTeleport(
                    player,
                    "§cTeletransporte cancelado: has recibido daño."
            );
        }
    }

    public void cancelTeleport(Player player, String message) {
        PendingTeleport pending =
                pendingTeleports.remove(player.getUniqueId());

        if (pending == null) {
            return;
        }

        if (pending.taskHandler != null) {
            pending.taskHandler.cancel();
            pending.taskHandler = null;
        }

        player.sendMessage(message);
    }

    public void cancelAll() {
        for (PendingTeleport pending : pendingTeleports.values()) {
            if (pending.taskHandler != null) {
                pending.taskHandler.cancel();
                pending.taskHandler = null;
            }
        }

        pendingTeleports.clear();
    }

    private void completeTeleport(
            Player player,
            Location destination,
            String homeName
    ) {
        if (!player.isOnline()) {
            return;
        }

        Level level = destination.getLevel();

        if (level == null) {
            player.sendMessage(
                    "§cEl mundo de destino ya no está disponible."
            );
            return;
        }

        Location safeLocation = findSafeLocation(destination);

        if (safeLocation == null) {
            player.sendMessage(
                    "§cNo se encontró una ubicación segura cerca de esta casa."
            );
            player.sendMessage(
                    "§7Intenta establecer la casa en otro lugar."
            );
            return;
        }

        if (player.teleport(safeLocation)) {
            player.sendMessage(
                    "§aTe has teletransportado a §e"
                            + homeName + "§a."
            );
        } else {
            player.sendMessage(
                    "§cNo se pudo completar el teletransporte."
            );
        }
    }

    private Location findSafeLocation(Location destination) {
        Level level = destination.getLevel();

        if (level == null) {
            return null;
        }

        int baseX = (int) Math.floor(destination.getX());
        int baseY = (int) Math.floor(destination.getY());
        int baseZ = (int) Math.floor(destination.getZ());

        // Busca primero en la posición guardada y por encima.
        for (int dy = 0; dy <= 3; dy++) {
            for (int radius = 0; radius <= 3; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz))
                                != radius) {
                            continue;
                        }

                        int x = baseX + dx;
                        int y = baseY + dy;
                        int z = baseZ + dz;

                        if (isSafePosition(level, x, y, z)) {
                            return new Location(
                                    x + 0.5,
                                    y,
                                    z + 0.5,
                                    destination.getYaw(),
                                    destination.getPitch(),
                                    level
                            );
                        }
                    }
                }
            }
        }

        // Si no encuentra espacio, prueba hasta dos bloques por debajo.
        for (int dy = 1; dy <= 2; dy++) {
            int y = baseY - dy;

            for (int radius = 0; radius <= 3; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz))
                                != radius) {
                            continue;
                        }

                        int x = baseX + dx;
                        int z = baseZ + dz;

                        if (isSafePosition(level, x, y, z)) {
                            return new Location(
                                    x + 0.5,
                                    y,
                                    z + 0.5,
                                    destination.getYaw(),
                                    destination.getPitch(),
                                    level
                            );
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isSafePosition(Level level, int x, int y, int z) {
        if (y < 1) {
            return false;
        }

        Block ground = level.getBlock(x, y - 1, z);
        Block feet = level.getBlock(x, y, z);
        Block head = level.getBlock(x, y + 1, z);

        // Necesita suelo sólido y no peligroso.
        if (!ground.isSolid() || isDangerousBlock(ground)) {
            return false;
        }

        // Necesita espacio libre para el cuerpo y la cabeza.
        if (feet.isSolid() || head.isSolid()) {
            return false;
        }

        return !isDangerousBlock(feet)
                && !isDangerousBlock(head);
    }

    private boolean isDangerousBlock(Block block) {
        int id = block.getId();

        // IDs clásicos de agua, lava, fuego, cactus y magma.
        if (id == 8 || id == 9
                || id == 10 || id == 11
                || id == 51 || id == 81
                || id == 213) {
            return true;
        }

        String name = block.getName().toLowerCase(Locale.ROOT);

        return name.contains("lava")
                || name.contains("fire")
                || name.contains("cactus")
                || name.contains("magma")
                || name.contains("campfire")
                || name.contains("sweet_berry")
                || name.contains("powder_snow");
    }

    private static class PendingTeleport {

        private final Location destination;
        private final String homeName;

        private int remainingSeconds;
        private TaskHandler taskHandler;

        private PendingTeleport(
                Location destination,
                String homeName,
                int remainingSeconds
        ) {
            this.destination = destination;
            this.homeName = homeName;
            this.remainingSeconds = remainingSeconds;
        }
    }
}
