package org.example.Services;

import org.example.Entities.GptChatMessage;

import java.util.ArrayList;
import java.util.Comparator;

public class GptChatMessageService {
    public static ArrayList<GptChatMessage> sortMessages(ArrayList<GptChatMessage> messagesList) {
        messagesList.sort(new Comparator<GptChatMessage>() {
            public int compare(GptChatMessage mes1, GptChatMessage mes2) {
                return mes1.getMessageDate().compareTo(mes2.getMessageDate());
            }
        });
        return messagesList;
    }
}
