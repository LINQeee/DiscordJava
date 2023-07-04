package org.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.example.Commands.BotCommands;
import org.example.Events.EventsListener;
import org.example.Services.MySqlService;

public class Main {

    public static Role BannedRole;

    public static void main(String[] args) throws InterruptedException {
        var jda = JDABuilder.createDefault("MTEwODM5MjkyNjIxNzc3MzE5Nw.GiBZEN.s0EWcTCjlnYn6WBkEWcxei-r5G8ZlmTN1qYz-0")
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .setActivity(Activity.playing("Answering The Questions"))
                .addEventListeners(new EventsListener())
                .addEventListeners(new BotCommands())
                .build().awaitReady();
        addCommands(jda);

        MySqlService.setupGuilds(jda.getGuilds());
    }

    private static void addCommands(JDA jda) {
        jda.upsertCommand("askgpt", "Asks chat gpt with your prompt").addOption(OptionType.STRING, "prompt", "Запрос chat gpt на который он ответит", true).queue();
        jda.upsertCommand("imagegpt", "Generates an image from the prompt").addOption(OptionType.STRING, "prompt", "Запрос для генерации картинки", true).queue();
        jda.upsertCommand("randomimage", "Generates a random image from the prompt").queue();
        jda.upsertCommand("banned_role", "Setup what role is banned role").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).addOption(OptionType.ROLE, "role", "banned role", true).queue();
        jda.upsertCommand("api_key", "Open AI api key").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)).addOption(OptionType.STRING, "key", "api key", true).queue();
        jda.upsertCommand("create_chat", "Creating Chat Gpt chat").queue();
        jda.upsertCommand("delete_chat", "Deleting current gpt chat").queue();
    }
}