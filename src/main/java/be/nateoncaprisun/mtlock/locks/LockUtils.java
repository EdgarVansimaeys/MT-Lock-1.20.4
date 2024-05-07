package be.nateoncaprisun.mtlock.locks;

import be.nateoncaprisun.mtlock.MTLock;
import be.nateoncaprisun.mtlock.datatype.SerializableLock;
import be.nateoncaprisun.mtlock.listeners.LockInteractionListener;
import be.nateoncaprisun.mtlock.utils.ChatUtils;
import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class LockUtils {

    @Getter
    private static final PersistentDataType<?, SerializableLock> lockType = new ConfigurationSerializableDataType(SerializableLock.class);

    public void createLock(Block block, Player owner) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        SerializableLock lock = new SerializableLock(owner.getUniqueId(), List.of(owner.getUniqueId()));

        pdc.set(LockInteractionListener.OWNER_KEY, lockType, lock);
    }

    public void deleteLock(Block block) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        pdc.remove(LockInteractionListener.OWNER_KEY);
    }

    public void addMember(Block block, OfflinePlayer member) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        SerializableLock lock = LockUtils.getLock(block);

        List<UUID> list = new ArrayList<>();

        for (UUID uuid : lock.getMembers()) {
            list.add(uuid);
        }

        list.add(member.getUniqueId());

        lock.setMembers(list);
        pdc.set(LockInteractionListener.OWNER_KEY, lockType, lock);
    }

    public void removeMember(Block block, OfflinePlayer member) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        SerializableLock lock = LockUtils.getLock(block);

        List<UUID> list = new ArrayList<>();

        for (UUID uuid : lock.getMembers()) {
            list.add(uuid);
        }

        list.remove(member.getUniqueId());

        lock.setMembers(list);
        pdc.set(LockInteractionListener.OWNER_KEY, lockType, lock);
    }

    public void setOwner(OfflinePlayer owner, Block block) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        SerializableLock lock = new SerializableLock(owner.getUniqueId(), List.of(owner.getUniqueId()));

        pdc.set(LockInteractionListener.OWNER_KEY, lockType, lock);
    }

    public void addGroup(String group, Block block, Player player) {
        if (MTLock.getInstance().getLockGroups().get(block.getLocation()) == null) {
            MTLock.getInstance().getLockGroups().put(block.getLocation(), List.of(group));
            player.sendMessage(ChatUtils.color("<green>Group geadd."));
            return;
        }

        if (MTLock.getInstance().getLockGroups().get(block.getLocation()).contains(group)) {
            player.sendMessage(ChatUtils.color("<red>Deze group is al geadd op deze lock."));
            return;
        }

        List<String> string = MTLock.getInstance().getLockGroups().get(block.getLocation());
        string.add(group);
        MTLock.getInstance().getLockGroups().remove(block.getLocation());
        MTLock.getInstance().getLockGroups().put(block.getLocation(), string);

        player.sendMessage(ChatUtils.color("<green>Group geadd."));
    }

    public void removeGroup(String group, Block block, Player player) {
        if (MTLock.getInstance().getLockGroups().get(block.getLocation()) == null) {
            player.sendMessage(ChatUtils.color("<red>Deze group staat niet op deze lock."));
            return;
        }

        if (!MTLock.getInstance().getLockGroups().get(block.getLocation()).contains(group)) {
            player.sendMessage(ChatUtils.color("<red>Deze group staat niet op deze lock."));
            return;
        }

        List<String> string = MTLock.getInstance().getLockGroups().get(block.getLocation());
        string.remove(group);
        MTLock.getInstance().getLockGroups().remove(block.getLocation());
        MTLock.getInstance().getLockGroups().put(block.getLocation(), string);

        player.sendMessage(ChatUtils.color("<green>Group removed."));
    }

    public SerializableLock getLock(Block block) {
        PersistentDataContainer pdc = new CustomBlockData(block, MTLock.getInstance());
        return pdc.get(LockInteractionListener.OWNER_KEY, lockType);
    }

}
