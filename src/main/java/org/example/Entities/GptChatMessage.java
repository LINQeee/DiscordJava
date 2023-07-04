package org.example.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class GptChatMessage {
    private String channelId;
    private String messageRole;
    private String messageContent;
    private Date messageDate;
}
