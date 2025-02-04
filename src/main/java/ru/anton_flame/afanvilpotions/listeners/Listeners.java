package ru.anton_flame.afanvilpotions.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.anton_flame.afanvilpotions.AFAnvilPotions;
import ru.anton_flame.afanvilpotions.utils.Hex;

public class Listeners implements Listener {
    private final AFAnvilPotions plugin;
    public Listeners(AFAnvilPotions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (firstItem != null && secondItem != null && firstItem.getType() == Material.POTION && secondItem.getType() == Material.POTION) {
            PotionMeta firstMeta = (PotionMeta) firstItem.getItemMeta();
            PotionMeta secondMeta = (PotionMeta) secondItem.getItemMeta();
            PotionEffectType firstType = firstMeta.getBasePotionData().getType().getEffectType();
            PotionEffectType secondType = secondMeta.getBasePotionData().getType().getEffectType();

            if (firstType != null && secondType != null && firstType == secondType && firstMeta.getBasePotionData().isUpgraded() && secondMeta.getBasePotionData().isUpgraded()) {
                boolean needUpgrade = false;

                if (plugin.getConfig().getBoolean("settings.check-permission")) {
                    for (HumanEntity entity : event.getViewers()) {
                        if (!entity.hasPermission("afanvilpotions.upgrade")) {
                            event.setResult(null);
                            return;
                        } else {
                            needUpgrade = true;
                        }
                    }
                } else {
                    needUpgrade = true;
                }

                if (needUpgrade) {
                    ConfigurationSection potionsSection = plugin.getConfig().getConfigurationSection("settings.potions");
                    for (String potion : potionsSection.getKeys(false)) {
                        if (firstType.getName().equalsIgnoreCase(potion)) {
                            ItemStack potionItem = new ItemStack(Material.POTION, 1);
                            PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potion), potionsSection.getInt(potion + ".duration"), 2), true);
                            potionMeta.displayName(Component.text(Hex.color(potionsSection.getString(potion + ".potion-name"))));
                            potionItem.setItemMeta(potionMeta);

                            event.setResult(potionItem);
                            inventory.setRepairCost(potionsSection.getInt(potion + ".exp-price"));
                            return;
                        }
                    }
                }
            }
        }
    }
}
