package snowsan0113.discord_connect.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import snowsan0113.discord_connect.manager.discord.DiscordManager;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String send_format = String.format("[Minecraft] <%s>: %s", player.getName(), message);
        DiscordManager.getSendChannel().sendMessage(send_format).queue();
    }
}
