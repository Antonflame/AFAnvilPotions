package ru.anton_flame.afanvilpotions.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ConfigManager {

    public static boolean checkPermission;
    public static String noPermission, reloaded;
    public static List<String> help;
    public static ConfigurationSection potions;

    public static void setupConfigValues(Plugin plugin) {
        checkPermission = plugin.getConfig().getBoolean("settings.check-permission");
        potions = plugin.getConfig().getConfigurationSection("settings.potions");
        noPermission = Hex.color(plugin.getConfig().getString("messages.no-permission"));
        reloaded = Hex.color(plugin.getConfig().getString("messages.reloaded"));
        help = Hex.color(plugin.getConfig().getStringList("messages.help"));
    }
}
