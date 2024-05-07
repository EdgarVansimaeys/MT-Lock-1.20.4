package be.nateoncaprisun.mtlock.utils;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.function.Predicate;

public class WorldguardUtils {

    public static ProtectedRegion getRegion(@Nonnull Location location, Predicate<Integer> priorityPredicate) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        ProtectedRegion region = null;
        if (manager != null) {
            ApplicableRegionSet fromRegions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
            region = fromRegions.getRegions().stream().filter(protectedRegion -> priorityPredicate.test(protectedRegion.getPriority()))
                    .max(Comparator.comparing(ProtectedRegion::getPriority)).orElse(null);
        }
        return region;
    }

}

