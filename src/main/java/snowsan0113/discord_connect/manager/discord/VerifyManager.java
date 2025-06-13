package snowsan0113.discord_connect.manager.discord;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import snowsan0113.discord_connect.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VerifyManager {

    private static final List<Verify> verify_list = new ArrayList<>();

    public static void startVerify(OfflinePlayer player) {
        if (getVerify(player) == null) {
            Verify verify = new Verify(player);
            verify_list.add(verify);
        }
    }

    public static int stopVerify(OfflinePlayer player, int code) {
        Verify verify = getVerify(player);
        if (verify != null) {
            if (code == verify.getCode()) {
                verify.stopVerify();
                return 0;
            }
            else {
                return -1;
            }
        }
        return -2;
    }

    public static Verify getVerify(OfflinePlayer player) {
        return verify_list.stream().filter(Verify::isEnable).findFirst().orElse(null);
    }

    public static class Verify {
        private final OfflinePlayer player;
        private final int code;
        private int time;
        private final BukkitTask task;
        private boolean is_enable;

        public Verify(OfflinePlayer player) {
            this.player = player;

            this.code = new Random().nextInt(10000);
            this.time = 60*5;
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (time <= 0) {
                        is_enable = false;
                        this.cancel();
                    }
                    else {
                        time--;
                    }
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0L, 20L);

            this.is_enable = true;
        }

        public OfflinePlayer getPlayer() {
            return player;
        }

        public int getCode() {
            return code;
        }

        public void stopVerify() {
            task.cancel();
            is_enable = false;
        }

        public boolean isEnable() {
            return is_enable;
        }
    }
}
