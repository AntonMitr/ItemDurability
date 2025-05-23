package by.kotor.item.durability.event;

import by.kotor.item.durability.Plugin;
import by.kotor.item.durability.util.PlayerItemKey;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class DurabilityListener implements Listener {
    private final Plugin plugin;
    private final HashMap<PlayerItemKey, Long> cooldowns;
    private final String WARNING_TITLE = ChatColor.RED + "Осторожно!";
    private final String WARNING_SUBTITLE = ChatColor.WHITE + "Предмет ломается! Прочность: %d/%d";

    public DurabilityListener(Plugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        String key = "ITEM_" + event.getItem().getType().name() + "_" + event.getItem().getType().hashCode();
        checkDurability(event.getPlayer(), event.getItem(), key);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        String key = "MAIN_HAND_" + item.getType().name() + "_" + item.getType().hashCode();
        checkDurability(player, item, key);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            ItemStack item = armor[i];
            if (item != null && item.getType().getMaxDurability() != 0) {
                String key = "ARMOR_" + "_" + item.getType().name() + "_" + item.getType().hashCode();
                checkDurability(player, item, key);
            }
        }
    }

    private void checkDurability(Player player, ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return;

        int currentDamage = damageable.getDamage();
        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability == 0) return;

        int currentDurability = maxDurability - currentDamage;
        double percentage = (double) currentDurability/maxDurability * 100;
        double warningPercentage = plugin.getConfig().getDouble("warning-percentage", 10);

        if (percentage <= warningPercentage) {
            UUID playerId = player.getUniqueId();
            PlayerItemKey playerItemKey = new PlayerItemKey(playerId, key);
            Long currentTime = System.currentTimeMillis();
            Long lastNotification = cooldowns.get(playerItemKey);

            if (lastNotification == null || currentTime - lastNotification >= 5000) {
                player.sendTitle(WARNING_TITLE, String.format(WARNING_SUBTITLE, currentDurability, maxDurability),
                        10, 60, 10);

                cooldowns.put(playerItemKey, currentTime);
            }
        }
    }

}
