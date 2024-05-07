package be.nateoncaprisun.mtlock.commands;

import be.nateoncaprisun.mtlock.MTLock;
import be.nateoncaprisun.mtlock.utils.ChatUtils;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("unlock")
public class UnlockCommand extends BaseCommand {

    @Default
    public void deleteLock(Player player) {
        MTLock main = MTLock.getInstance();
        if (main.getRemovingLocks().contains(player)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Already-Unlocking")));
            return;
        }

        main.getRemovingLocks().add(player);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Delete-Lock")));
    }

}
