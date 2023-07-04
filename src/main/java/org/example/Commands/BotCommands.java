package org.example.Commands;

import com.theokanning.openai.OpenAiHttpException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.example.Entities.GptChat;
import org.example.Entities.GptChatMessage;
import org.example.Services.AiService;
import org.example.Services.GptChatMessageService;
import org.example.Services.MySqlService;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.EnumSet;

public class BotCommands extends ListenerAdapter {


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String apiKey = MySqlService.getApiKey(event.getGuild().getId());

        if (event.getName().equals("api_key")) {
            if (event.getMember().isOwner()) {
                MySqlService.setApiKey(event.getGuild().getId(), event.getOption("key").getAsString());
                event.reply("Api key has been set").queue();
            } else {
                event.reply("You don't have permission").queue();
            }
        } else if (event.getName().equals("banned_role")) {
            if (event.getMember().isOwner()) {
                MySqlService.setBannedRoleId(event.getGuild().getId(), event.getOption("role").getAsRole().getId());
                event.reply("Role has been set").queue();
            } else {
                event.reply("You don't have permission").queue();
            }
        } else if (event.getName().equals("askgpt")) {
            if (!isChannelPrivate(event, true)) return;
            event.deferReply().queue();
            try {
                ArrayList<String> gptAnswer = AiService.sendChatRequest(new GptChatMessage(event.getChannel().getId(), "user", event.getOption("prompt").getAsString(), null), apiKey);
                event.getHook().sendMessage(event.getUser().getName() + ": " + event.getOption("prompt").getAsString()).queue();
                for (String response : gptAnswer){
                    event.getChannel().sendMessage(response).queue();
                }
            } catch (OpenAiHttpException | NullPointerException e) {
                event.getHook().sendMessage("Set api key!").setEphemeral(true).queue();
            }
        } else if (event.getName().equals("create_chat")) {
            if (MySqlService.isGptChatExist(new GptChat(event.getUser().getId(), event.getGuild().getId()))) {
                event.reply("You already have a chat, use \"delete_chat\" command to delete it").setEphemeral(true).queue();
                return;
            }
            event.reply("Creating chat...").setEphemeral(true).queue();
            event.getGuild().createTextChannel(event.getUser().getName() + " chat with gpt", event.getChannel().asTextChannel().getParentCategory())
                    .addPermissionOverride(event.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                    .addPermissionOverride(event.getGuild().getOwner(), EnumSet.of(Permission.ADMINISTRATOR), null)
                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.USE_APPLICATION_COMMANDS), null)
                    .addPermissionOverride(event.getGuild().getRolesByName("chatgptbot", true).get(0), EnumSet.of(Permission.ADMINISTRATOR), null)
                    .queue();
            MySqlService.insertGptChat(new GptChat(event.getUser().getId(), event.getGuild().getId()));
        } else if (event.getName().equals("delete_chat")) {
            if (MySqlService.isGptChatExist(new GptChat(event.getUser().getId(), event.getGuild().getId())) && !isChannelPrivate(event, false)) {
                event.reply("You don't have chat or you are using this command in not a chat channel").setEphemeral(true).queue();
                return;
            }
            MySqlService.deleteGptChat(new GptChat(event.getUser().getId(), event.getGuild().getId()));
            MySqlService.deleteGptDialogMessages(event.getChannel().getId());
            event.getChannel().delete().queue();
        }
    }

    private boolean isChannelPrivate(SlashCommandInteractionEvent event, boolean isMustReply) {
        int channelUserCount = 0;
        for (var member : event.getChannel().asTextChannel().getMembers()) {
            if (!member.getUser().isBot()) channelUserCount++;
        }
        if (channelUserCount > 2) {
            if (isMustReply) event.reply("Use \"create chat\" command at first").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
