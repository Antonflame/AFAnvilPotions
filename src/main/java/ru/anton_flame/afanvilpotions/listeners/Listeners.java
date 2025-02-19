package ru.anton_flame.afanvilpotions.listeners;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.anton_flame.afanvilpotions.utils.ConfigManager;
import ru.anton_flame.afanvilpotions.utils.Hex;

import java.util.HashMap;
import java.util.Map;

public class Listeners implements Listener {

    private final Map<ItemStack, ItemStack> remainingItems = new HashMap<>();

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (firstItem != null && secondItem != null && firstItem.getType().toString().contains("POTION") && secondItem.getType() == firstItem.getType()) {
            PotionMeta firstMeta = (PotionMeta) firstItem.getItemMeta();
            PotionMeta secondMeta = (PotionMeta) secondItem.getItemMeta();
            PotionEffectType firstType = firstMeta.getBasePotionData().getType().getEffectType();
            PotionEffectType secondType = secondMeta.getBasePotionData().getType().getEffectType();

            if (firstType == secondType) {
                if (firstMeta.getBasePotionData().isUpgraded() && secondMeta.getBasePotionData().isUpgraded()) {
                    if (ConfigManager.enabledLevel3) {
                        upgradePotion(event, inventory, ConfigManager.checkPermissionLevel3, "afanvilpotions.upgrade.level.3", firstItem, secondItem, ConfigManager.potionsLevel3, 2);
                    }
                } else if (!firstMeta.getBasePotionData().isUpgraded() && !secondMeta.getBasePotionData().isUpgraded()) {
                    if (ConfigManager.enabledLevel2) {
                        upgradePotion(event, inventory, ConfigManager.checkPermissionLevel2, "afanvilpotions.upgrade.level.2", firstItem, secondItem, ConfigManager.potionsLevel2, 1);
                    }
                }
            }
        }
    }

    private void upgradePotion(PrepareAnvilEvent event, AnvilInventory inventory, boolean checkPermission, String permission, ItemStack firstItem, ItemStack secondItem, ConfigurationSection potions, int level) {
        PotionMeta firstMeta = (PotionMeta) firstItem.getItemMeta();
        PotionEffectType firstType = firstMeta.getBasePotionData().getType().getEffectType();

        boolean needUpgrade = false;
        if (checkPermission) {
            for (HumanEntity entity : event.getViewers()) {
                if (!entity.hasPermission(permission)) {
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
            for (String potion : potions.getKeys(false)) {
                if (firstType.getName().equalsIgnoreCase(potion)) {
                    ItemStack potionItem = new ItemStack(firstItem.getType(), 1);
                    PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(potion), potions.getInt(potion + ".duration") * 20, level), true);
                    potionMeta.setDisplayName(Hex.color(potions.getString(potion + ".potion-name")));

                    String[] color = potions.getString(potion + ".potion-color").split(",");
                    int r = Integer.parseInt(color[0]);
                    int g = Integer.parseInt(color[1]);
                    int b = Integer.parseInt(color[2]);
                    potionMeta.setColor(Color.fromRGB(r, g, b));

                    int firstItemAmount = firstItem.getAmount();
                    int secondItemAmount = secondItem.getAmount();
                    ItemStack remainingItem = firstItemAmount > secondItemAmount ? secondItem.clone() : firstItem.clone();
                    int remainingAmount = firstItemAmount > secondItemAmount ? firstItemAmount - secondItemAmount : secondItemAmount - firstItemAmount;

                    remainingItem.setAmount(remainingAmount);
                    if (firstItemAmount > 1 && secondItemAmount > 1) {
                        potionItem.setAmount(Math.min(firstItemAmount, secondItemAmount));
                    }

                    potionItem.setItemMeta(potionMeta);
                    event.setResult(potionItem);
                    inventory.setRepairCost(potions.getInt(potion + ".exp-price") * potionItem.getAmount());

                    if (remainingAmount > 0 && !remainingItems.containsKey(potionItem)) {
                        remainingItems.put(potionItem, remainingItem);
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvil = (AnvilInventory) event.getInventory();

            if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack result = anvil.getResult();
                if (result != null) {
                    if (remainingItems.containsKey(result)) {
                        event.getWhoClicked().getInventory().addItem(remainingItems.get(result));
                        remainingItems.remove(result);
                    }
                }
            }
        }
    }
}
