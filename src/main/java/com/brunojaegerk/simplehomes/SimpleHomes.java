package com.brunojaegerk.simplehomes;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import com.formconstructor.FormConstructor;
import com.formconstructor.form.SimpleForm;
import com.formconstructor.form.CustomForm;
import com.formconstructor.form.element.custom.Input;

import com.brunojaegerk.simplehomes.commands.SetHomeCommand;
import com.brunojaegerk.simplehomes.commands.HomeCommand;
import com.brunojaegerk.simplehomes.commands.DelHomeCommand;
import com.brunojaegerk.simplehomes.homes.Home;
import com.brunojaegerk.simplehomes.homes.HomeManager;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SimpleHomes extends PluginBase {

    private DatabaseManager databaseManager;
    private HomeManager homeManager;
    private HomeTeleportManager homeTeleportManager;
    private Config lang;

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
            loadLang();

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
                    this,
                    databaseManager,
                    homeLimits,
                    defaultLimit,
                    opUnlimited
            );

            int teleportDelay = getConfig().getInt(
                    "settings.teleport-delay",
                    3
            );

            boolean cancelOnMove = getConfig().getBoolean(
                    "settings.teleport-cancel-on-move",
                    true
            );

            boolean cancelOnDamage = getConfig().getBoolean(
                    "settings.teleport-cancel-on-damage",
                    true
            );

            homeTeleportManager = new HomeTeleportManager(
                    this,
                    teleportDelay,
                    cancelOnMove,
                    cancelOnDamage
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
                    getMessage("general.player-only")
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
                    getMessage("errors.home-list-error")
            );
        }

        return true;
    }

    private void openHomesMenu(Player player, List<Home> homes) {
        int limit = homeManager.getHomeLimit(player);

        String limitText = limit == Integer.MAX_VALUE
                ? getMessage("menus.homes.unlimited")
                : String.valueOf(limit);

        String content;

        if (homes.isEmpty()) {
            content = getMessage("menus.homes.empty");
        } else {
            content = getMessage("menus.homes.select-home");
        }

        content += getMessage(
                "menus.homes.count",
                "current", String.valueOf(homes.size()),
                "limit", limitText
        );

        SimpleForm form = new SimpleForm(
                getMessage("menus.homes.title"),
                content
        );

        for (Home home : homes) {
            String homeName = home.getName();

            form.addButton(
                    getMessage(
                            "menus.homes.home-button",
                            "home", homeName
                    ),
                    (formPlayer, button) ->
                            openHomeActions(
                                    (Player) formPlayer,
                                    homeName
                            )
            );
        }

        form.addButton(
                getMessage("menus.homes.close"),
                (formPlayer, button) ->
                        formPlayer.sendMessage(
                                getMessage("menus.homes.closed")
                        )
        );

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void openHomeActions(Player player, String homeName) {
        SimpleForm form = new SimpleForm(
                getMessage(
                        "menus.actions.title",
                        "home", homeName
                ),
                getMessage("menus.actions.content")
        );

        form.addButton(
                getMessage("menus.actions.teleport"),
                (formPlayer, button) ->
                        teleportToHome((Player) formPlayer, homeName)
        );

        form.addButton(
                getMessage("menus.actions.rename"),
                (formPlayer, button) ->
                        openRenameHomeForm((Player) formPlayer, homeName)
        );

        form.addButton(
                getMessage("menus.actions.delete"),
                (formPlayer, button) ->
                        openDeleteHomeConfirmation((Player) formPlayer, homeName)
        );

        form.addButton(
                getMessage("menus.actions.back"),
                (formPlayer, button) -> {
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
                                getMessage("menus.actions.reload-error")
                        );
                    }
                }
        );

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void openRenameHomeForm(Player player, String oldName) {
        CustomForm form = new CustomForm(
                getMessage("menus.rename.title")
        );

        form.addElement(
                "newName",
                new Input(
                        getMessage("menus.rename.input-label"),
                        getMessage("menus.rename.input-placeholder"),
                        oldName
                )
        );

        form.setHandler((formPlayer, response) -> {
            Player target = formPlayer;
            String newName = response.getInput("newName").getValue();

            if (newName == null || newName.trim().isEmpty()) {
                target.sendMessage(
                        getMessage("menus.rename.empty")
                );
                return;
            }

            newName = newName.trim();

            if (newName.length() > 32
                    || !newName.matches("[a-zA-Z0-9_-]+")) {
                target.sendMessage(
                        getMessage("menus.rename.invalid")
                );
                return;
            }

            if (oldName.equals(newName)) {
                target.sendMessage(
                        getMessage("menus.rename.unchanged")
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
                            getMessage("menus.rename.failed")
                    );
                    return;
                }

                target.sendMessage(
                        getMessage(
                                "menus.rename.success",
                                "old_home", oldName,
                                "new_home", newName
                        )
                );

                openHomesMenu(
                        target,
                        homeManager.getHomes(target.getUniqueId())
                );

            } catch (IllegalArgumentException exception) {
                target.sendMessage(
                        TextFormat.colorize(
                                '&',
                                "&c" + exception.getMessage()
                        )
                );

            } catch (SQLException exception) {
                getLogger().error(
                        "Error SQLite al renombrar el hogar: "
                                + exception.getMessage()
                );

                target.sendMessage(
                        getMessage("menus.rename.error")
                );
            }
        });

        FormConstructor.getInstance()
                .getFormService()
                .sendForm(player, form);
    }

    private void openDeleteHomeConfirmation(
            Player player,
            String homeName
    ) {
        SimpleForm form = new SimpleForm(
                getMessage("menus.delete.title"),
                getMessage(
                        "menus.delete.confirmation",
                        "home", homeName
                )
        );

        form.addButton(
                getMessage("menus.delete.confirm"),
                (formPlayer, button) -> {
                    Player target = (Player) formPlayer;

                    try {
                        boolean deleted = homeManager.deleteHome(
                                target.getUniqueId(),
                                homeName
                        );

                        if (deleted) {
                            target.sendMessage(
                                    getMessage(
                                            "menus.delete.success",
                                            "home", homeName
                                    )
                            );
                        } else {
                            target.sendMessage(
                                    getMessage(
                                            "menus.delete.missing",
                                            "home", homeName
                                    )
                            );
                        }

                        openHomesMenu(
                                target,
                                homeManager.getHomes(target.getUniqueId())
                        );

                    } catch (SQLException exception) {
                        getLogger().error(
                                "Error SQLite al eliminar el hogar desde el menú: "
                                        + exception.getMessage()
                        );

                        target.sendMessage(
                                getMessage("menus.delete.error")
                        );
                    }
                }
        );

        form.addButton(
                getMessage("menus.delete.cancel"),
                (formPlayer, button) ->
                        openHomeActions((Player) formPlayer, homeName)
        );

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
                        getMessage("errors.home-missing")
                );
                return;
            }

            Level level = getServer().getLevelByName(
                    home.getWorldName()
            );

            if (level == null) {
                player.sendMessage(
                        getMessage("commands.home.world-unavailable")
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
                        getMessage("errors.teleport-unavailable")
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
                    getMessage("errors.home-query-error")
            );
        }
    }

    private void loadLang() {
        saveResource("lang.yml", false);

        lang = new Config(
                new File(getDataFolder(), "lang.yml"),
                Config.YAML
        );
    }

    public String getMessage(String key) {
        if (lang == null) {
            return "§cMensaje no disponible: " + key;
        }

        String message = lang.getString(key);

        if (message == null) {
            getLogger().warning(
                    "No se encontró la clave de idioma: " + key
            );
            return "§cMensaje no disponible: " + key;
        }

        return TextFormat.colorize('&', message);
    }

    public String getMessage(
            String key,
            String... replacements
    ) {
        String message = getMessage(key);

        if (replacements == null) {
            return message;
        }

        if (replacements.length % 2 != 0) {
            getLogger().warning(
                    "Reemplazos incompletos para la clave: " + key
            );
            return message;
        }

        for (int i = 0; i < replacements.length; i += 2) {
            String placeholder = "{" + replacements[i] + "}";
            String value = replacements[i + 1] == null
                    ? ""
                    : replacements[i + 1];

            message = message.replace(placeholder, value);
        }

        return message;
    }
}


