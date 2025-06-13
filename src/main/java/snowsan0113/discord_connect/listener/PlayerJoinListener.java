package snowsan0113.discord_connect.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import snowsan0113.discord_connect.manager.discord.VerifyManager;
import snowsan0113.discord_connect.manager.discord.WhitelistManager;

import java.io.IOException;

public class PlayerJoinListener implements Listener {
    
    @EventHandler
    public void onJoin(PlayerLoginEvent event) throws IOException {
        Player player = event.getPlayer();

        if (WhitelistManager.isWhiteList(player)) {
            event.allow();
        }
        else {
            VerifyManager.startVerify(player);
            VerifyManager.Verify verify = VerifyManager.getVerify(player);
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST,
                    "あなたはホワイトリストに登録されていません。" + "\n" +
                    "認証コード: " + verify.getCode());
        }
    }
    
}
