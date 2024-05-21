package be.nateoncaprisun.mtlock.commands;

import be.nateoncaprisun.mtlock.MTLock;
import be.nateoncaprisun.mtlock.datatype.SerializableLock;
import be.nateoncaprisun.mtlock.listeners.LockInteractionListener;
import be.nateoncaprisun.mtlock.utils.ChatUtils;
import be.nateoncaprisun.mtlock.locks.LockUtils;
import be.nateoncaprisun.mtlock.utils.Utils;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.UUID;

@CommandAlias("lock")
public class LockCommand extends BaseCommand {

    @Default
    public void createLock(Player player) {
        MTLock main = MTLock.getInstance();

        if (main.getBuildingLocks().contains(player)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Already-Locking")));
            return;
        }

        main.getBuildingLocks().add(player);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Create-Lock")));
    }

    @HelpCommand
    public void help(Player player) {
        if (!player.hasPermission("lock.admin") && !player.isOp()) {
            player.sendMessage(Utils.color("&8&m---------------------------------"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&8&l» &6CraftVille-Lock"));
            player.sendMessage(Utils.color("&8&l» &fVersie: &e1.2.0"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&7/lock help &8➠ &fKrijg dit bericht"));
            player.sendMessage(Utils.color("&7/lock &8➠ &fMaak een lock"));
            player.sendMessage(Utils.color("&7/unlock &8➠ &fVerwijder een lock"));
            player.sendMessage(Utils.color("&7/lock addmember &o<speler> &8➠ &fVoeg een speler toe aan een lock"));
            player.sendMessage(Utils.color("&7/lock removemember &o<speler> &8➠ &fVerwijder een speler van een lock"));
            player.sendMessage(Utils.color("&7/lock info &8➠ &fBekijk informatie over een lock"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&8&m---------------------------------"));
        } else {
            player.sendMessage(Utils.color("&8&m---------------------------------"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&8&l» &6CraftVille-Lock"));
            player.sendMessage(Utils.color("&8&l» &fVersie: &e1.2.0"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&7/lock help &8➠ &fKrijg dit bericht"));
            player.sendMessage(Utils.color("&7/lock &8➠ &fMaak een lock"));
            player.sendMessage(Utils.color("&7/unlock &8➠ &fVerwijder een lock"));
            player.sendMessage(Utils.color("&7/lock setowner &o<speler> &8➠ &fZet een owner van een lock"));
            player.sendMessage(Utils.color("&7/lock addmember &o<speler> &8➠ &fVoeg een speler toe aan een lock"));
            player.sendMessage(Utils.color("&7/lock removemember &o<speler> &8➠ &fVerwijder een speler van een lock"));
            player.sendMessage(Utils.color("&7/lock addgroup &o<group> &8➠ &fVoeg een group toe aan een lock"));
            player.sendMessage(Utils.color("&7/lock removegroup &o<group> &8➠ &fVerwijder een group van een lock"));
            player.sendMessage(Utils.color("&7/lock info &8➠ &fBekijk informatie over een lock"));
            player.sendMessage("");
            player.sendMessage(Utils.color("&8&m---------------------------------"));
        }
    }

    @Subcommand("addmember")
    public void addMember(Player player, OfflinePlayer member) {
        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);
        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        SerializableLock lock = LockUtils.getLock(block);

        if (!container.has(LockInteractionListener.OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        if (!lock.getOwner().equals(player.getUniqueId()) && !player.hasPermission("lock.admin")) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Owner")));
            return;
        }

        if (lock.getMembers().contains(member.getUniqueId())) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Already-Member")));
            return;
        }

        LockUtils.addMember(block, member);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Player-Added").replaceAll("%player%", member.getName())));
    }

    @Subcommand("removemember")
    public void removeMember(Player player, OfflinePlayer member) {
        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);
        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        SerializableLock lock = LockUtils.getLock(block);

        if (!container.has(LockInteractionListener.OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        if (!lock.getOwner().equals(player.getUniqueId()) && !player.hasPermission("lock.admin")) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Owner")));
            return;
        }

        if (!lock.getMembers().contains(member.getUniqueId())) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Member")));
            return;
        }

        LockUtils.removeMember(block, member);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Removed-Member").replaceAll("%player%", member.getName())));
    }

    @Subcommand("setowner")
    @CommandPermission("lock.admin")
    public void setOwner(Player player, OfflinePlayer onlinePlayer) {
        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);
        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        if (!container.has(LockInteractionListener.OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        LockUtils.setOwner(onlinePlayer, block);
        player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Set-Owner-Lock").replaceAll("%player%", onlinePlayer.getName())));
    }

    @Subcommand("info")
    public void lockInfo(Player player) {

        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Lock")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);

        SerializableLock lock = LockUtils.getLock(block);

        if (lock == null) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        player.sendMessage(ChatUtils.color("<green>Lock Eigenaar: <dark_green>" + Bukkit.getOfflinePlayer(lock.getOwner()).getName()));

        if (lock.getMembers() != null && !lock.getMembers().isEmpty()) {
            player.sendMessage(ChatUtils.color("<green>Lock Members: "));
            for (UUID memberId : lock.getMembers()) {
                if (memberId.equals(lock.getOwner())) continue;
                player.sendMessage(ChatUtils.color("<green> - <dark_green>" + Bukkit.getOfflinePlayer(memberId).getName()));
            }
        }

        if (MTLock.getInstance().getLockGroups().get(block.getLocation()) != null && !MTLock.getInstance().getLockGroups().isEmpty()) {
            player.sendMessage(ChatUtils.color("<green>Lock Groups: "));
            for (String s : MTLock.getInstance().getLockGroups().get(block.getLocation())) {
                player.sendMessage(ChatUtils.color("<green> - <dark_green>" + s));
            }
        }
    }

    @Subcommand("addgroup")
    @CommandPermission("lock.admin")
    public void addGroup(Player player, String group) {
        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);
        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        SerializableLock lock = LockUtils.getLock(block);

        if (!container.has(LockInteractionListener.OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        if (!lock.getOwner().equals(player.getUniqueId()) && !player.hasPermission("lock.admin")) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Owner")));
            return;
        }

        LockUtils.addGroup(group, block, player);
    }

    @Subcommand("removegroup")
    @CommandPermission("lock.admin")
    public void removegroup(Player player, String group) {
        Block target = player.getTargetBlockExact(5);

        if (target == null || target.getType().equals(Material.AIR)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("Unlockable-Block")));
            return;
        }

        Block block = MTLock.getInstance().doorCheck(player, target);
        PersistentDataContainer container = new CustomBlockData(block, MTLock.getInstance());

        SerializableLock lock = LockUtils.getLock(block);

        if (!container.has(LockInteractionListener.OWNER_KEY)) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Lock")));
            return;
        }

        if (!lock.getOwner().equals(player.getUniqueId()) && !player.hasPermission("lock.admin")) {
            player.sendMessage(ChatUtils.color(MTLock.getInstance().getConfig().getString("No-Owner")));
            return;
        }

        LockUtils.removeGroup(group, block, player);
    }

}
