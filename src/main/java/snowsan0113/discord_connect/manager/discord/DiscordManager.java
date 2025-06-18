package snowsan0113.discord_connect.manager.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import snowsan0113.discord_connect.Main;
import snowsan0113.discord_connect.command.discord.MinecraftJoinCommand;
import snowsan0113.discord_connect.command.discord.WhiteListInfoCommand;

public class DiscordManager extends ListenerAdapter {

    private static JDA jda;
    private static final Main instance;
    private static final FileConfiguration config;

    static {
        instance = Main.getPlugin(Main.class);
        config = instance.getConfig();
    }

    public static void startBot() throws InterruptedException {
        if (jda == null) {
            String token = config.getString("bot_token");
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new DiscordManager())
                    .addEventListeners(new MinecraftJoinCommand())
                    .addEventListeners(new WhiteListInfoCommand())
                    .build();

            jda.updateCommands()
                    .addCommands(Commands.slash("mc-join", "サーバーに参加するコマンド")
                            .addOptions(
                                    new OptionData(OptionType.STRING, "mcid", "マインクラフトID"),
                                    new OptionData(OptionType.INTEGER, "code", "認証コード")))
                    .addCommands(Commands.slash("whitelist-info", "ホワイトリストの情報")
                            .addOption(OptionType.STRING, "whitelist-value", "マイクラID"))
                    .queue();

            jda.awaitReady();

            Bukkit.getLogger().info("[DiscordConnect] Botの準備ができました!");
        }
    }

    public static void stopBot() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }

    public static JDA getJDA() {
        return jda;
    }

    public static TextChannel getSendChannel() {
        String channel_id = config.getString("send_channel_id");
        return jda.getTextChannelById(channel_id);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Member member = event.getMember();
        Message message = event.getMessage();
        TextChannel send_channel = DiscordManager.getSendChannel();
        JDA jda = event.getJDA();
        SelfUser self = jda.getSelfUser();

        if (member != null) {
            User user = member.getUser();
            if (self.getIdLong() != user.getIdLong()) {
                String message_format = String.format("[Discord] <%s>: %s", user.getName(), message);
                Bukkit.broadcastMessage(message_format);
            }
        }
    }

}
