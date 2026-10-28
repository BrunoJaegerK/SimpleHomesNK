package com.brunojaegerk.simplehomes.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

import com.brunojaegerk.simplehomes.SimpleHomes;
import com.brunojaegerk.simplehomes.homes.Home;
import com.brunojaegerk.simplehomes.homes.HomeManager;

import java.sql.SQLException;

public class HomeCommand extends Command {

    private final SimpleHomes plugin;
    private final HomeManager homeManager;

    public HomeCommand(SimpleHomes plugin, HomeManager homeManager) {
        super(
                "home",
                "Teletransporta a un hogar guardado",
                "/home [nombre]"
        );

        this.plugin = plugin;
        this.homeManager = homeManager;

        setPermission("simplehomes.command.home");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    plugin.getMessage("general.player-only")
            );
            return true;
        }

        Player player = (Player) sender;

        if (!testPermission(player)) {
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(
                    plugin.getMessage("commands.home.usage")
            );
            return true;
        }

        String homeName = args.length == 0 ? "casa" : args[0];

        try {
            Home home = homeManager.getHome(
                    player.getUniqueId(),
                    homeName
            );

            if (home == null) {
                player.sendMessage(
                        plugin.getMessage(
                                "commands.home.not-found",
                                "home", homeName
                        )
                );
                return true;
            }

            Level level = plugin.getServer().getLevelByName(
                    home.getWorldName()
            );

            if (level == null) {
                player.sendMessage(
                        plugin.getMessage(
                                "commands.home.world-unavailable"
                        )
                );
                return true;
            }

            Location destination = new Location(
                    home.getX(),
                    home.getY(),
                    home.getZ(),
                    home.getYaw(),
                    home.getPitch(),
                    level
            );

            if (plugin.getTeleportManager() == null) {
                player.sendMessage(
                        plugin.getMessage("errors.teleport-unavailable")
                );
                return true;
            }

            plugin.getTeleportManager().teleport(
                    player,
                    destination,
                    home.getName()
            );

        } catch (IllegalArgumentException exception) {
            player.sendMessage("§c" + exception.getMessage());

        } catch (SQLException exception) {
            plugin.getLogger().error(
                    "Error SQLite al buscar un hogar: "
                            + exception.getMessage()
            );

            player.sendMessage(
                    plugin.getMessage("commands.home.lookup-error")
            );
        }

        return true;
    }
}


