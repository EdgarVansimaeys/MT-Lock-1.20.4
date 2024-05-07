package be.nateoncaprisun.mtlock;

import be.nateoncaprisun.mtlock.commands.LockCommand;
import be.nateoncaprisun.mtlock.commands.UnlockCommand;
import be.nateoncaprisun.mtlock.listeners.LockBreakListener;
import be.nateoncaprisun.mtlock.listeners.LockInteractionListener;
import be.nateoncaprisun.mtlock.utils.ConfigurationFile;
import be.nateoncaprisun.mtlock.locks.LocationSerialize;
import be.nateoncaprisun.mtlock.utils.WorldguardUtils;
import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Predicate;

public final class MTLock extends JavaPlugin {

    private static @Getter MTLock instance;
    private @Getter PaperCommandManager commandManager;
    private final @Getter List<Player> buildingLocks = new ArrayList<>();
    private final @Getter List<Player> removingLocks = new ArrayList<>();
    private @Getter ConfigurationFile lockFile;
    private @Getter HashMap<Location, List<String>> lockGroups = new HashMap<>();

    private final Predicate<Integer> priorityPredicate = priority -> priority >= 0;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        lockFile = new ConfigurationFile(this, "lock.yml", true);
        lockFile.saveConfig();

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new LockCommand());
        commandManager.registerCommand(new UnlockCommand());

        Bukkit.getPluginManager().registerEvents(new LockInteractionListener(), this);
        Bukkit.getPluginManager().registerEvents(new LockBreakListener(), this);

        registerGroups();
    }

    @Override
    public void onDisable() {
        saveConfig();
        lockFile.saveConfig();
        saveGroups();
    }

    public boolean isLockable(Block block) {
        if (block.getBlockData() instanceof Door) return true;
        if (block.getBlockData() instanceof TrapDoor) return true;
        if (block.getBlockData() instanceof Gate) return true;
        if (block.getState() instanceof Chest) return true;
        if (block.getState() instanceof Sign) return true;
        if (block.getState() instanceof Furnace) return true;
        if (block.getState() instanceof Barrel) return true;
        return false;
    }

    public Block doorCheck(Player player, Block block) {
        if (block.getBlockData() instanceof Door) {
            Bisected data = (Bisected) block.getBlockData();
            if (data.getHalf() == Bisected.Half.BOTTOM) {
                Location loc = block.getLocation();
                block = player.getWorld().getBlockAt(loc.add(0, 1, 0));
            }
            return block;
        }
        return block;
    }

    public Boolean regionCheck(Player player, Block block) {
        if (player.hasPermission("lock.admin")) return true;
        ProtectedRegion protectedRegion = WorldguardUtils.getRegion(block.getLocation(), priorityPredicate);
        if (protectedRegion == null) return false;
        if (protectedRegion.isOwner(WorldGuardPlugin.inst().wrapPlayer(player))) return true;
        return false;
    }

    public static boolean isPlayerInGroup(Player player, String group) {
        return player.hasPermission("group." + group);
    }

    public static String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }

    public void registerGroups() {
        if (lockFile.getConfig().getConfigurationSection("locks") == null) return;
        for (String s : lockFile.getConfig().getConfigurationSection("locks").getKeys(false)) {
            if (lockFile.getConfig().getStringList("locks."+s) == null) continue;
            lockGroups.put(LocationSerialize.toLocation(s), lockFile.getConfig().getStringList("locks."+s));
        }
    }

    public void saveGroups() {
        lockFile.getConfig().set("locks", null);

        if (lockGroups == null) return;

        lockGroups.forEach(((location, list) -> {
            String loc = LocationSerialize.toString(location);
            lockFile.getConfig().set("locks."+loc, list);
        }));
        lockFile.saveConfig();
    }
}
