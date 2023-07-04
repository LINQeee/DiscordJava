package org.example.Events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.example.Services.MySqlService;

import java.util.ArrayList;

public class EventsListener extends ListenerAdapter {

    @Override
    @SubscribeEvent
    public void onGuildLeave(GuildLeaveEvent e) {
        MySqlService.removeGuild(e.getGuild().getId());
    }

    @Override
    @SubscribeEvent
    public void onGuildJoin(GuildJoinEvent e) {
        MySqlService.setupGuilds(e.getJDA().getGuilds());
    }

    @Override
    @SubscribeEvent
    public void onGuildUpdateName(GuildUpdateNameEvent e) {
        MySqlService.setGuildName(e.getGuild().getId(), e.getGuild().getName());
    }

    @Override
    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent e) {

        if (e.getMessage().getContentRaw().equalsIgnoreCase("!admin-guilds")) {
            ArrayList<String> guildsId = MySqlService.getGuildsId();
            if (guildsId.isEmpty()) {
                e.getMessage().reply("There is no guilds in db").queue();
            } else {
                StringBuilder message = new StringBuilder();
                for (String guildId : guildsId)
                    message.append("\nGUILD: ").append(MySqlService.getGuildName(guildId)).append("\nId: ").
                            append(guildId).
                            append("\nbanned_role_id: ").
                            append(MySqlService.getBannedRoleId(guildId));
                e.getMessage().reply(message).queue();
            }
        }
        try {
            Role bannedRole = e.getGuild().getRoleById(MySqlService.getBannedRoleId(e.getGuild().getId()));
            if (!e.getMember().getRoles().isEmpty() && e.getMember().getRoles().contains(bannedRole)) {
                if (e.getMessage().getContentRaw().equalsIgnoreCase("")) {
                    e.getChannel().deleteMessageById(e.getMessageId()).queue();
                    e.getGuild().removeRoleFromMember(e.getMember(), bannedRole).queue();
                    e.getMessage().reply("разбан").queue();
                } else {
                    e.getChannel().deleteMessageById(e.getMessageId()).queue();
                    e.getMessage().reply("забанен " + e.getMember().getNickname()).queue();
                }

            }
        } catch (IllegalArgumentException error) {
            System.out.println("Set banned role id!");
        }
    }
}
