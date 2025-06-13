package snowsan0113.discord_connect.manager.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import snowsan0113.discord_connect.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class WhitelistManager {

    private static final Gson gson;

    static  {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static void addWhiteList(OfflinePlayer player, String id) throws IOException {
        JsonObject raw_json = getJson();
        if (!raw_json.has(player.getName())) {
            JsonObject player_json = new JsonObject();
            player_json.addProperty("UUID", player.getUniqueId().toString());
            player_json.addProperty("discord_id", id);

            raw_json.add(player.getName(), player_json);

            writeFile(gson.toJson(raw_json));
        }
    }

    public static boolean isWhiteList(OfflinePlayer player) throws IOException {
        JsonObject raw_json = getJson();
        return raw_json.has(player.getName());
    }

    public static JsonObject getJson() throws IOException {
        createJson();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(getFile().toPath()), StandardCharsets.UTF_8))) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) {
                writeFile("{}");
            }
            return json;
        }
    }

    public static void writeFile(String date) {
        try (BufferedWriter write = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getFile().toPath()), StandardCharsets.UTF_8))) {
            write.write(date);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createJson() throws IOException {
        if (!getFile().exists()) {
            getFile().createNewFile();
            writeFile("{}");
        }
    }

    private static File getFile() {
        return new File(Main.getPlugin(Main.class).getDataFolder(), "discord_whitelist.json");
    }

}
