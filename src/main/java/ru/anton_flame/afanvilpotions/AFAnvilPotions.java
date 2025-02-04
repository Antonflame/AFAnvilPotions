package ru.anton_flame.afanvilpotions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.anton_flame.afanvilpotions.listeners.Listeners;

public final class AFAnvilPotions extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Плагин был включен!");
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин был выключен!");
    }
}
