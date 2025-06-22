package snowsan0113.discord_connect.manager.discord;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import snowsan0113.discord_connect.Main;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLManager {

    //プラグイン
    private Main instance = null;
    private FileConfiguration config = null;

    //SQL
    private Connection con;

    //データベース情報
    private static String DRIVER_NAME; //ドライバーの名前
    private static String DB_HOST; //ホスト（IP）
    private static String DB_PORT; //ポート
    private static String JDBC_URL; //接続したいURL
    private static String USER_ID; //ログインしたいID
    private static String USER_PASS; //ログインしたいユーザーパスワード

    public SQLManager() throws IOException {
        instance = Main.getPlugin(Main.class);
        config = instance.getConfig();

        DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
        DB_HOST = config.getString("sql.host");
        DB_PORT = config.getString("sql.port");
        JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/testdb?characterEncoding=UTF-8&serverTimezone=Asia/Tokyo";
        USER_ID = config.getString("sql.user");
        USER_PASS = config.getString("sql.password");

        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnect() {
        if (isDatabaseMode()) {
            if (con != null) {
                return con;
            }
            else {
                try {
                    con = DriverManager.getConnection(JDBC_URL, USER_ID, USER_PASS);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public int save(UUID uuid, String discordID) throws SQLException {
        if (isDatabaseMode()) {
            try {
                getConnect().setAutoCommit(false);

                PreparedStatement checkStmt = getConnect().prepareStatement(
                        "SELECT * FROM player_links WHERE uuid = ? AND discord_id = ?"
                );
                checkStmt.setString(1, uuid.toString());
                checkStmt.setString(2, discordID);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    //すでに登録済み
                    return -1;
                }

                // プレイヤー登録
                PreparedStatement playerStmt = getConnect().prepareStatement(
                        "INSERT IGNORE INTO minecraft_players (uuid, created_at) VALUES (?, ?)"
                );
                playerStmt.setString(1, uuid.toString());
                playerStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                playerStmt.executeUpdate();

                // Discordユーザー登録
                PreparedStatement discordStmt = getConnect().prepareStatement(
                        "INSERT IGNORE INTO discord_users (discord_id, created_at) VALUES (?, ?)"
                );
                discordStmt.setString(1, discordID);
                discordStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                discordStmt.executeUpdate();

                PreparedStatement linkStmt = getConnect().prepareStatement(
                        "INSERT INTO player_links (uuid, discord_id, linked_at) VALUES (?, ?, ?)"
                );
                linkStmt.setString(1, uuid.toString());
                linkStmt.setString(2, discordID);
                linkStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                linkStmt.executeUpdate();

                getConnect().commit();
                return 0;
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                getConnect().rollback();
            }

            return -2;
        }
        else {
            return -3;
        }
    }

    public static boolean isDatabaseMode() {
        return Main.getPlugin(Main.class).getConfig().getBoolean("sql.save_database", false);
    }

    public List<WhitelistManager.DiscordWhiteList> getWhiteList() throws SQLException {
        PreparedStatement stmt = getConnect().prepareStatement("SELECT uuid, discord_id FROM player_links");
        ResultSet rs = stmt.executeQuery();

        List<WhitelistManager.DiscordWhiteList> list = new ArrayList<>();
        while (rs.next()) {
            String uuid = rs.getString("uuid");
            String discordId = rs.getString("discord_id");
            System.out.println("UUID: " + UUID.fromString(uuid).toString());
            System.out.println("DiscordID: " + discordId);
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            User user = DiscordManager.getJDA().retrieveUserById(discordId).complete();
            list.add(new WhitelistManager.DiscordWhiteList(player, user));
        }
        return list;
    }

    public List<User> getUsers(UUID uuid) throws SQLException {
        PreparedStatement stmt = getConnect().prepareStatement(
                "SELECT discord_id FROM player_links WHERE uuid = ?"
        );
        stmt.setString(1, uuid.toString());
        ResultSet rs = stmt.executeQuery();

        List<User> list = new ArrayList<>();
        while (rs.next()) {
            String discordId = rs.getString("discord_id");
            User user = DiscordManager.getJDA().retrieveUserById(discordId).complete();
            list.add(user);
        }
        return list;
    }

    public List<UUID> getUUIDs(User user) throws SQLException {
        PreparedStatement stmt = getConnect().prepareStatement(
                "SELECT uuid FROM player_links WHERE discord_id = ?"
        );
        stmt.setString(1, user.getId());
        ResultSet rs = stmt.executeQuery();

        List<UUID> list = new ArrayList<>();
        while (rs.next()) {
            String uuid_string = rs.getString("uuid");
            UUID uuid = UUID.fromString(uuid_string);
            list.add(uuid);
        }
        return list;
    }

}

