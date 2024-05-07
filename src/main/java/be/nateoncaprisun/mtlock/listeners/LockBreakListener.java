package be.nateoncaprisun.mtlock.listeners;

import be.nateoncaprisun.mtlock.MTLock;
import be.nateoncaprisun.mtlock.datatype.SerializableLock;
import be.nateoncaprisun.mtlock.utils.ChatUtils;
import be.nateoncaprisun.mtlock.locks.LockUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LockBreakListener implements Listener {

    @EventHandler
    public void lockBreak(BlockBreakEvent event) {
        Block target = event.getBlock();
        Player player = event.getPlayer();
        Block block = MTLock.getInstance().doorCheck(player, target);

        SerializableLock lock = LockUtils.getLock(block);

        if (lock == null) return;

        if (!player.hasPermission("lock.build") || !player.hasPermission("lock.admin")) {
            event.setCancelled(true);
        }

        LockUtils.deleteLock(block);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Lock-Break")));
    }

}
