
package com.brunojaegerk.simplehomes.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;

import com.brunojaegerk.simplehomes.homes.HomeManager;
import com.brunojaegerk.simplehomes.SimpleHomes;

import java.sql.SQLException;

public class SetHomeCommand extends Command {

    private final HomeManager homeManager;
    private final SimpleHomes plugin;

    public SetHomeCommand(
            SimpleHomes plugin,
            HomeManager homeManager
    ) {
        super(
                "sethome",
                "Guarda un hogar en tu ubicación actual",
                "/sethome [nombre]"
        );

        this.plugin = plugin;
        this.homeManager = homeManager;

        setPermission("simplehomes.command.sethome");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    "Este comando solo puede ejecutarse dentro del juego."
            );
            return true;
        }

        Player player = (Player) sender;

        if (!testPermission(player)) {
            return true;
        }

        String homeName = args.length == 0 ? "casa" : args[0];

        if (args.length > 1) {
            player.sendMessage("§cUso correcto: /sethome [nombre]");
            return true;
        }

        try {
            // No permitir duplicados ni contar un hogar existente
            // como una nueva creación.
            if (homeManager.homeExists(
                    player.getUniqueId(),
                    homeName
            )) {
                player.sendMessage(
                        "§cYa tienes un hogar con ese nombre."
                );
                return true;
            }

            int limit = homeManager.getHomeLimit(player);
            int currentHomes = homeManager.getHomeCount(
                    player.getUniqueId()
            );

            if (limit != Integer.MAX_VALUE
                    && currentHomes >= limit) {
                player.sendMessage(
                        "§cHas alcanzado tu límite de hogares "
                                + "§e(" + currentHomes + "/" + limit + ")§c."
                );
                player.sendMessage(
                        "§7Elimina un hogar con §e/delhome [nombre] "
                                + "§7para liberar espacio."
                );
                return true;
            }

            Position position = player.getPosition();

            boolean created = homeManager.setHome(
                    player.getUniqueId(),
                    homeName,
                    player.getLevel().getName(),
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    (float) player.getYaw(),
                    (float) player.getPitch()
            );

            if (created) {
                player.sendMessage(
                        "§aHogar §e" + homeName
                                + " §aguardado correctamente."
                );
            } else {
                player.sendMessage(
                        "§cYa tienes un hogar con ese nombre."
                );
            }

        } catch (IllegalArgumentException exception) {
            player.sendMessage("§c" + exception.getMessage());

        } catch (SQLException exception) {
            plugin.getLogger().error(
                    "Error SQLite al guardar un hogar: "
                            + exception.getMessage()
            );

            player.sendMessage(
                    "§cNo se pudo guardar el hogar. Inténtalo de nuevo."
            );
        }

        return true;
    }
}


