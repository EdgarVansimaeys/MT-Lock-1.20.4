package be.nateoncaprisun.mtlock.datatype;

import co.aikar.commands.annotation.Optional;
import lombok.Data;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class SerializableLock implements ConfigurationSerializable {

    private UUID owner;
    private List<UUID> members;

    public SerializableLock(UUID owner, List<UUID> members) {
        this.owner = owner;
        this.members = members;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("owner", owner.toString());
        map.put("members", members);
        return map;
    }

    public static SerializableLock deserialize(Map<String,Object> map) {
        UUID owner = UUID.fromString((String) map.get("owner"));
        List<UUID> members = (List<UUID>) map.get("members");
        return new SerializableLock(owner, members);
    }

    @Override
    public String toString() {
        return "SerializableLock{owner='" + owner + "', members=" + members + "}";
    }
}
