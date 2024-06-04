package org.tacsbot.api.notification;

import org.apache.http.HttpException;
import org.tacsbot.model.NotificationDTO;

import java.io.IOException;
import java.util.List;


public interface NotificationApi {

    List<NotificationDTO> getPendingNotifications() throws HttpException, IOException;

    boolean markAsNotified(String notificationId) throws HttpException, IOException;

}
