package org.tacsbot.api.notification.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.apache.http.HttpException;
import org.tacsbot.api.notification.NotificationApi;
import org.tacsbot.model.NotificationDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

public class NotificationApiConnection implements NotificationApi {
    @Setter
    private NotificationHttpConnector notificationHttpConnector = new NotificationHttpConnector();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NotificationDTO> getPendingNotifications() throws HttpException, IOException {
        try {
            HttpResponse<String> response = notificationHttpConnector.getPendingNotificationsConnector();
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, NotificationDTO.class));
            } else {
                throw new HttpException("Error getting pending notifications: " + response.body());
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new HttpException("Exception getting pending notifications: " + e.getMessage());
        }
    }

    @Override
    public boolean markAsNotified(String notificationId) throws HttpException, IOException {
        try {
            HttpResponse<String> response = notificationHttpConnector.markAsNotifiedConnector(notificationId);
            return response.statusCode() == 200;
        } catch (URISyntaxException | InterruptedException e) {
            throw new HttpException("Exception marking notification as notified: " + e.getMessage());
        }
    }
}
