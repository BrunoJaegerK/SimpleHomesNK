package com.brunojaegerk.simplehomes.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import com.brunojaegerk.simplehomes.SimpleHomes;
import com.brunojaegerk.simplehomes.homes.HomeManager;

import java.sql.SQLException;

public class DelHomeCommand extends Command {

    private final SimpleHomes plugin;
    private final HomeManager homeManager;

    public DelHomeCommand(SimpleHomes plugin, HomeManager homeManager) {
        super(
                "delhome",
                "Elimina uno de tus hogares guardados",
                "/delhome <nombre>"
        );

        this.plugin = plugin;
        this.homeManager = homeManager;

        setPermission("simplehomes.command.delhome");
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

        if (args.length != 1) {
            player.sendMessage("§cUso correcto: /delhome <nombre>");
            return true;
        }

        String homeName = args[0];

        try {
            boolean deleted = homeManager.deleteHome(
                    player.getUniqueId(),
                    homeName
            );

            if (deleted) {
                player.sendMessage(
                        "§aEl hogar §e" + homeName
                                + " §ase ha eliminado correctamente."
                );
            } else {
                player.sendMessage(
                        "§cNo tienes ningún hogar llamado §e"
                                + homeName + "§c."
                );
            }

        } catch (IllegalArgumentException exception) {
            player.sendMessage("§c" + exception.getMessage());

        } catch (SQLException exception) {
            plugin.getLogger().error(
                    "Error SQLite al eliminar el hogar: "
                            + exception.getMessage()
            );

            player.sendMessage(
                    "§cNo se pudo eliminar el hogar. Inténtalo de nuevo."
            );
        }

        return true;
    }
}

