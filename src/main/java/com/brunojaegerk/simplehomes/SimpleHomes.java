package com.brunojaegerk.simplehomes;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;

import com.formconstructor.FormConstructor;
import com.formconstructor.form.SimpleForm;
import com.formconstructor.form.CustomForm;
import com.formconstructor.form.element.custom.Input;

import com.brunojaegerk.simplehomes.commands.SetHomeCommand;
import com.brunojaegerk.simplehomes.commands.HomeCommand;
import com.brunojaegerk.simplehomes.commands.DelHomeCommand;
import com.brunojaegerk.simplehomes.homes.Home;
import com.brunojaegerk.simplehomes.homes.HomeManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SimpleHomes extends PluginBase {

    private DatabaseManager databaseManager;
    private HomeManager homeManager;
    private HomeTeleportManager homeTeleportManager;

    @Override
    public void onEnable() {
        getLogger().info("Iniciando SimpleHomes...");

        if (FormConstructor.getInstance() == null) {
            getLogger().error(
                    "FormConstructor no está disponible. "
                            + "Comprueba que esté instalado y habilitado."
            );

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            saveDefaultConfig();

            databaseManager = new DatabaseManager(getDataFolder());
            databaseManager.connect();

            Map<String, Object> homeLimits =
                    getConfig().getSection("home-limits").getAll();

            int defaultLimit = getConfig().getInt(
                    "settings.default-limit",
                    3
            );

            boolean opUnlimited = getConfig().getBoolean(
                    "settings.op-unlimited",
                    true
            );

            homeManager = new HomeManager(
                    databaseManager,
                    homeLimits,
                    defaultLimit,
                    opUnlimited
            );

            int teleportDelay = getConfig().getInt(
                    "settings.teleport-delay",
                    3
            );

            homeTeleportManager = new HomeTeleportManager(
                    this,
                    teleportDelay
            );

            getServer().getPluginManager().registerEvents(
                    homeTeleportManager,
                    this
            );

            getServer().getCommandMap().register(
                    "simplehomes",
                    new SetHomeCommand(this, homeManager)
            );

            getServer().getCommandMap().register(
                    "simplehomes",
                    new HomeCommand(this, homeManager)
            );

            getServer().getCommandMap().register(
                    "simplehomes",
                    new DelHomeCommand(this, homeManager)
            );

        } catch (SQLException | RuntimeException exception) {
            getLogger().error(
                    "No se pudo iniciar SimpleHomes: "
                            + exception.getMessage()
            );

            if (homeTeleportManager != null) {
                homeTeleportManager.cancelAll();
            }

            if (databaseManager != null) {
                databaseManager.close();
            }

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("SQLite conectado correctamente.");
        getLogger().info("SimpleHomes se ha habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        if (homeTeleportManager != null) {
            homeTeleportManager.cancelAll();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("SimpleHomes se ha deshabilitado.");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public HomeTeleportManager getTeleportManager() {
        return homeTeleportManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!command.getName().equalsIgnoreCase("homes")) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    "Este comando solo puede ejecutarse dentro del juego."
            );
            return true;
        }

        Player player = (Player) sender;

        try {
            List<Home> homes = homeManager.getHomes(
                    player.getUniqueId()
            );

            openHomesMenu(player, homes);

        } catch (SQLException exception) {
            getLogger().error(
                    "Error SQLite al cargar los hogares: "
                            + exception.getMessage()
            );

            player.sendMessage(
                    "§cNo se pudieron cargar tus hogares. Inténtalo de nuevo."
            );
        }

        return true;
    }

    private void openHomesMenu(Player player, List<Home> homes) {
        int limit = homeManager.getHomeLimit(player);

        String limitText = limit == Integer.MAX_VALUE
                ? "Ilimitados"
                : String.valueOf(limit);

        String content;

        if (homes.isEmpty()) {
            content = "§7Todavía no tienes hogares guardados.\n\n"
                    + "§fUsa §e/sethome [nombre] §fpara crear uno.";
        } else {
            content = "§7Selecciona un hogar para ver sus opciones.";
        }

        content += "\n\n§fHogares guardados: §e"
                + homes.size() + "§f/§e" + limitText;

        SimpleForm form = new SimpleForm(
                "§l§6Mis hogares",
                content
        );

        for (Home home : homes) {
            String homeName = home.getName();

            form.addButton(
                    "§e🏠 " + homeName,
                    (formPlayer, button) ->
                            openHomeActions(
                                    (Player) formPlayer,
                                    homeName
                            )
            );
        }

        form.addButton("§cCerrar", (formPlayer, button) ->
                formPlayer.sendMessage(
                        "§7Has cerrado el menú de hogares."
                )
        );

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void openHomeActions(Player player, String homeName) {
        SimpleForm form = new SimpleForm(
                "§l§6Hogar: " + homeName,
                "§7¿Qué deseas hacer con este hogar?"
        );

        form.addButton("§aTeletransportarme", (formPlayer, button) ->
                teleportToHome((Player) formPlayer, homeName)
        );

        form.addButton("§eCambiar nombre", (formPlayer, button) ->
                openRenameHomeForm((Player) formPlayer, homeName)
        );

        form.addButton("§cVolver", (formPlayer, button) -> {
            Player target = (Player) formPlayer;

            try {
                openHomesMenu(
                        target,
                        homeManager.getHomes(target.getUniqueId())
                );
            } catch (SQLException exception) {
                getLogger().error(
                        "Error al volver al menú de hogares: "
                                + exception.getMessage()
                );

                target.sendMessage(
                        "§cNo se pudo cargar el menú de hogares."
                );
            }
        });

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void openRenameHomeForm(Player player, String oldName) {
        CustomForm form = new CustomForm("§l§6Cambiar nombre");

        form.addElement(
                "newName",
                new Input(
                        "Nuevo nombre",
                        "Escribe el nuevo nombre",
                        oldName
                )
        );

        form.setHandler((formPlayer, response) -> {
            Player target = formPlayer;
            String newName = response.getInput("newName").getValue();

            if (newName == null || newName.trim().isEmpty()) {
                target.sendMessage(
                        "§cEl nombre del hogar no puede estar vacío."
                );
                return;
            }

            newName = newName.trim();

            if (newName.length() > 32
                    || !newName.matches("[a-zA-Z0-9_-]+")) {
                target.sendMessage(
                        "§cUsa un nombre de hasta 32 caracteres: "
                                + "letras, números, guion o guion bajo."
                );
                return;
            }

            if (oldName.equals(newName)) {
                target.sendMessage(
                        "§eEl nombre del hogar no ha cambiado."
                );
                return;
            }

            try {
                boolean renamed = homeManager.renameHome(
                        target.getUniqueId(),
                        oldName,
                        newName
                );

                if (!renamed) {
                    target.sendMessage(
                            "§cNo se pudo renombrar el hogar. "
                                    + "Comprueba que todavía exista."
                    );
                    return;
                }

                target.sendMessage(
                        "§aHogar renombrado correctamente: §e"
                                + oldName + " §7→ §e" + newName
                );

                openHomesMenu(
                        target,
                        homeManager.getHomes(target.getUniqueId())
                );

            } catch (IllegalArgumentException exception) {
                target.sendMessage(
                        "§c" + exception.getMessage()
                );

            } catch (SQLException exception) {
                getLogger().error(
                        "Error SQLite al renombrar el hogar: "
                                + exception.getMessage()
                );

                target.sendMessage(
                        "§cOcurrió un error al renombrar el hogar."
                );
            }
        });

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void teleportToHome(Player player, String homeName) {
        try {
            Home home = homeManager.getHome(
                    player.getUniqueId(),
                    homeName
            );

            if (home == null) {
                player.sendMessage(
                        "§cEse hogar ya no existe."
                );
                return;
            }

            Level level = getServer().getLevelByName(
                    home.getWorldName()
            );

            if (level == null) {
                player.sendMessage(
                        "§cEl mundo de ese hogar no está disponible."
                );
                return;
            }

            Location destination = new Location(
                    home.getX(),
                    home.getY(),
                    home.getZ(),
                    home.getYaw(),
                    home.getPitch(),
                    level
            );

            if (homeTeleportManager == null) {
                player.sendMessage(
                        "§cEl sistema de teletransporte no está disponible."
                );
                return;
            }

            homeTeleportManager.teleport(
                    player,
                    destination,
                    home.getName()
            );

        } catch (SQLException exception) {
            getLogger().error(
                    "Error SQLite al teletransportar al hogar: "
                            + exception.getMessage()
            );

            player.sendMessage(
                    "§cNo se pudo consultar ese hogar."
            );
        }
    }
}

