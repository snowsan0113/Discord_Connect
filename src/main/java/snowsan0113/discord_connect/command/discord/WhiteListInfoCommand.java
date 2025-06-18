package snowsan0113.discord_connect.command.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import snowsan0113.discord_connect.manager.discord.WhitelistManager;

import java.awt.*;
import java.io.IOException;

public class WhiteListInfoCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        Member member = event.getMember();
        MessageChannelUnion channel = event.getChannel();
        OptionMapping option = event.getOption("whitelist-value");

        if ("whitelist-info".equalsIgnoreCase(cmd)) {
            String player_option = option.getAsString();
            try {
                WhitelistManager.DiscordWhiteList whitelist = WhitelistManager.getWhiteList(Bukkit.getOfflinePlayer(player_option));
                EmbedBuilder embed = new EmbedBuilder();
                if (whitelist != null) {
                    embed.setTitle(player_option + "の情報");
                    embed.addField("名前", whitelist.getUser().getName(), true);
                    embed.addField("DiscordID", whitelist.getUser().getId(), true);
                }
                else {
                    embed.addField("エラー", "見つかりませんでした", true);
                    embed.setColor(Color.RED);
                }
                channel.sendMessageEmbeds(embed.build()).queue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

