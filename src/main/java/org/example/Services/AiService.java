package org.example.Services;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.example.Entities.GptChatMessage;
import org.example.Entities.RandomImageResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AiService {
//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
    public static ArrayList<String> sendChatRequest(GptChatMessage message, String apiKey) {
        message.setMessageContent(validateMessage(message.getMessageContent()));
        List<ChatMessage> messageList = new ArrayList<ChatMessage>();
        for (var gptChatMessage : GptChatMessageService.sortMessages(MySqlService.getGptDialogMessages(message.getChannelId()))){
            messageList.add(new ChatMessage(gptChatMessage.getMessageRole(), gptChatMessage.getMessageContent()));
        }
        messageList.add(new ChatMessage("user", message.getMessageContent()));

        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messageList)
                .build();

        String gptResponse = service.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();
        gptResponse = validateMessage(gptResponse);
        ArrayList<String> gptResponses = new ArrayList<String>();
        if (gptResponse.length() >= 1900){
            for (int i = 0; i < Math.floor((double) gptResponse.length() / 1900); i++){
                gptResponses.add(gptResponse.substring(i * 1900, (i+1) * 1900));
            }
            gptResponses.add(gptResponse.substring((int)Math.floor((double) gptResponse.length() / 1900)*1900));
        }
        else gptResponses.add(gptResponse);
        MySqlService.insertGptChatMessage(message);
        MySqlService.insertGptChatMessage(new GptChatMessage(message.getChannelId(), "assistant", gptResponse, null));
        return gptResponses;
    }

    public static String sendImageRequest(String prompt, String apiKey) {
        OpenAiService service = new OpenAiService(apiKey);

        CreateImageRequest imageRequest = CreateImageRequest.builder()
                .prompt(prompt)
                .n(1)
                .size("512x512")
                .build();
        return service.createImage(imageRequest).getData().get(0).getUrl();
    }

    public static RandomImageResponse generateRandomImageRequest(String apiKey) {
        //String imagePrompt = sendChatRequest("generate image prompt for dall-e ai and give me only the prompt itself without any other words and quotes", apiKey);

        //return new RandomImageResponse("Generated image with prompt: " + imagePrompt, sendImageRequest(imagePrompt, apiKey));
        return null;
    }

    private static String validateMessage(String message){
        message = message.replace("'", "\\'");
        message = message.replace("`", "\\'");
        message = message.replace("\\'\\'\\'", "```");
        return message;
    }
}
