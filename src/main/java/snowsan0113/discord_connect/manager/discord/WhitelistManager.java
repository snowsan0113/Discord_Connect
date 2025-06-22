package snowsan0113.discord_connect.manager.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import snowsan0113.discord_connect.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WhitelistManager {

    private static SQLManager sql;

    static {
        if (SQLManager.isDatabaseMode()) {
            try {
                sql = new SQLManager();
                sql.getConnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addWhiteList(OfflinePlayer player, String id) throws SQLException, IOException {
        UUID uuid = player.getUniqueId();
        if (getSaveMode().equalsIgnoreCase("SQL")) {
            sql.save(uuid, id);
        }
        else if (getSaveMode().equalsIgnoreCase("JSON")) {
            JsonManager.addWhiteList(Bukkit.getOfflinePlayer(uuid), id);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public static WhitelistManager.DiscordWhiteList getWhiteList(OfflinePlayer player) throws IOException, SQLException {
        if (getSaveMode().equalsIgnoreCase("SQL")) {
            return sql.getWhiteList()
                    .stream()
                    .filter(discordWhiteList -> discordWhiteList.getPlayer().getUniqueId().equals(player.getUniqueId()))
                    .findFirst()
                    .orElse(null);
        }
        else if (getSaveMode().equalsIgnoreCase("JSON")) {
            return JsonManager.getWhiteList(player);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public static List<WhitelistManager.DiscordWhiteList> getWhiteList() throws IOException, SQLException {
        if (getSaveMode().equalsIgnoreCase("SQL")) {
            return sql.getWhiteList();
        }
        else if (getSaveMode().equalsIgnoreCase("JSON")) {
            return JsonManager.getWhiteList();
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean isWhiteList(OfflinePlayer player) throws IOException, SQLException {
        if (getSaveMode().equalsIgnoreCase("SQL")) {
            return sql.getWhiteList()
                    .stream()
                    .anyMatch(discordWhiteList -> {
                        System.out.println(discordWhiteList.getPlayer().getUniqueId() + "　　　:　　　" + player.getUniqueId());
                                return discordWhiteList.getPlayer().getUniqueId().equals(player.getUniqueId());
                            }
                    );
        }
        else if (getSaveMode().equalsIgnoreCase("JSON")) {
            return JsonManager.isWhiteList(player);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    public static String getSaveMode() {
        if (sql != null) {
            return "SQL";
        }
        return "JSON";
    }

    public static class DiscordWhiteList {
        private final OfflinePlayer player;
        private final User user;

        public DiscordWhiteList(OfflinePlayer player, User user) {
            this.player = player;
            this.user = user;
        }

        public OfflinePlayer getPlayer() {
            return player;
        }

        public User getUser() {
            return user;
        }
    }

}
