package be.nateoncaprisun.mtlock.listeners;

import be.nateoncaprisun.mtlock.MTLock;
import be.nateoncaprisun.mtlock.datatype.SerializableLock;
import be.nateoncaprisun.mtlock.utils.ChatUtils;
import be.nateoncaprisun.mtlock.locks.LocationSerialize;
import be.nateoncaprisun.mtlock.locks.LockUtils;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Collection;

public class LockInteractionListener implements Listener {

    public static final NamespacedKey OWNER_KEY = new NamespacedKey(MTLock.getInstance(), "lock_owner");
    private final MTLock main = MTLock.getInstance();

    @EventHandler
    public void lockCreation(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block target = event.getClickedBlock();

        if (event.getHand() == null) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (target == null) return;
        if (!main.getBuildingLocks().contains(player)) return;
        main.getBuildingLocks().remove(player);

        Block block = MTLock.getInstance().doorCheck(player, target);

        if (!MTLock.getInstance().regionCheck(player ,target)){
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock-Possible")));
            return;
        }

        if (!main.isLockable(block)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        if (container.has(OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Already-Lock")));
            return;
        }

        LockUtils.createLock(block, player);
        event.setCancelled(true);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Lock-Created")));
    }

    @EventHandler
    public void lockDeletion(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block target = event.getClickedBlock();

        if (event.getHand() == null) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (target == null) return;
        if (!main.getRemovingLocks().contains(player)) return;
        main.getRemovingLocks().remove(player);

        Block block = MTLock.getInstance().doorCheck(player, target);

        if (!MTLock.getInstance().regionCheck(player ,target)){
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Cant-Delete")));
            return;
        }

        if (!main.isLockable(block)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Cant-Delete")));
            return;
        }

        SerializableLock lock = LockUtils.getLock(block);

        if (lock == null) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        if (!lock.getOwner().equals(player.getUniqueId()) && !player.hasPermission("lock.admin")) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Owner")));
            return;
        }

        LockUtils.deleteLock(block);
        event.setCancelled(true);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Lock-Deleted")));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lockInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block target = event.getClickedBlock();

        if (event.getHand() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (target == null) return;
        Block block = MTLock.getInstance().doorCheck(player, target);
        SerializableLock lock = LockUtils.getLock(block);
        if (lock == null) return;

        //player.sendMessage("Owner: " + (lock.getOwner().equals(player.getUniqueId())));
        //player.sendMessage("Member: " + (lock.getMembers().contains(player.getUniqueId())));

        Collection<String> stringCollection = MTLock.getInstance().getLockGroups().get(block.getLocation());
        if (stringCollection == null) stringCollection = new ArrayList<>();
        if (!lock.getOwner().equals(player.getUniqueId()) && !lock.getMembers().contains(player.getUniqueId()) && !player.hasPermission("lock.admin") && !stringCollection.contains(MTLock.getPlayerGroup(player, stringCollection))) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Not-Member")));
            event.setCancelled(true);
            return;
        }

        if (block.getType().equals(Material.IRON_DOOR) || block.getType().equals(Material.IRON_TRAPDOOR)) {
            Openable door = (Openable) block.getBlockData();
            door.setOpen(!door.isOpen());
            if (door.isOpen()) {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 3, 3);
            } else {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 3, 3);
            }
            block.setBlockData(door);
        }
        if (player.hasPermission("lock.admin")){
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Lock-Opening-Staff").replace("%player%", Bukkit.getOfflinePlayer(lock.getOwner()).getName())));
        } else {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Lock-Opening")));
        }
    }

}
