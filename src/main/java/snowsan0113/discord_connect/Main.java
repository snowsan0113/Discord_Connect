package snowsan0113.discord_connect;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import snowsan0113.discord_connect.listener.PlayerChatListener;
import snowsan0113.discord_connect.manager.discord.DiscordManager;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PluginManager plm = getServer().getPluginManager();
        plm.registerEvents(new PlayerChatListener(), this);

        try {
            DiscordManager.startBot();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("プラグインが有効になりました。");
    }

    @Override
    public void onDisable() {

    }
}
