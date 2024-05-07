package be.nateoncaprisun.mtlock.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

@UtilityClass
public class ChatUtils {

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public Component color(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }
//
//    public static final StringTemplate.Processor<Component, RuntimeException> MM = stringTemplate -> {
//        String interpolated = STR.process(stringTemplate);
//        return color(interpolated);
//    };

}
