package snowsan0113.discord_connect.command.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import snowsan0113.discord_connect.manager.discord.VerifyManager;
import snowsan0113.discord_connect.manager.discord.WhitelistManager;

import java.io.IOException;

public class MinecraftJoinCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        Member member = event.getMember();
        MessageChannelUnion channel = event.getChannel();

        if ("mc-join".equalsIgnoreCase(cmd)) {
            OptionMapping mcid = event.getOption("mcid");
            OptionMapping code = event.getOption("code");
            if (mcid != null && code != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(mcid.getAsString());
                int verify_status = VerifyManager.stopVerify(player, code.getAsInt());
                if (verify_status == 0) {
                    channel.sendMessage("認証に成功しました。").queue();
                    try {
                        WhitelistManager.addWhiteList(player, member.getId());
                        channel.sendMessage("ホワイトリストに登録することができました。").queue();
                    } catch (IOException e) {
                        channel.sendMessage("エラーが発生しました。エラー: " + e.getMessage()).queue();
                        throw new RuntimeException(e);
                    }
                }
                else {
                    channel.sendMessage("認証に失敗しました。エラーコード：" + verify_status).queue();
                }
            }
        }
    }
}
