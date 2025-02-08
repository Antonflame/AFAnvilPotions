package ru.anton_flame.afanvilpotions.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigManager {

    public static boolean enabledLevel2, enabledLevel3, checkPermissionLevel2, checkPermissionLevel3;
    public static String noPermission, reloaded;
    public static List<String> help;
    public static ConfigurationSection potionsLevel2, potionsLevel3;

    public static void setupConfigValues(Plugin plugin) {
        enabledLevel2 = plugin.getConfig().getBoolean("settings.level-2.enabled");
        enabledLevel3 = plugin.getConfig().getBoolean("settings.level-3.enabled");
        checkPermissionLevel2 = plugin.getConfig().getBoolean("settings.level-2.check-permission");
        checkPermissionLevel3 = plugin.getConfig().getBoolean("settings.level-3.check-permission");
        potionsLevel2 = plugin.getConfig().getConfigurationSection("settings.level-2.potions");
        potionsLevel3 = plugin.getConfig().getConfigurationSection("settings.level-3.potions");
        noPermission = Hex.color(plugin.getConfig().getString("messages.no-permission"));
        reloaded = Hex.color(plugin.getConfig().getString("messages.reloaded"));
        help = Hex.color(plugin.getConfig().getStringList("messages.help"));
    }
}
