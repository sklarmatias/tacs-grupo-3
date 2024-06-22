package org.tacsbot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificationDTO {
    private String id;
    private String type;
    private String articleName;
    private String subscriber;
    private boolean notified;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;
    private int currentSubscribers;
    private int minSubscribers;
    private int maxSubscribers;
}
