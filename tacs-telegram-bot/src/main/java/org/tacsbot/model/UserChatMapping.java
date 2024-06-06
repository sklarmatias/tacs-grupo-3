package org.tacsbot.model;
import java.util.HashMap;
import java.util.Map;

public class UserChatMapping {
    private final Map<String, Long> userIdToChatIdMap = new HashMap<>();
    private final Map<Long, String> chatIdToUserIdMap = new HashMap<>();

    public void addMapping(Long chatId, String userId) {
        userIdToChatIdMap.put(userId, chatId);
        chatIdToUserIdMap.put(chatId, userId);
    }

    public Long getChatId(String userId) {
        return userIdToChatIdMap.get(userId);
    }

    public String getUserId(Long chatId) {
        return chatIdToUserIdMap.get(chatId);
    }

    public void removeByUserId(String userId) {
        Long chatId = userIdToChatIdMap.remove(userId);
        if (chatId != null) {
            chatIdToUserIdMap.remove(chatId);
        }
    }

    public void removeByChatId(Long chatId) {
        String userId = chatIdToUserIdMap.remove(chatId);
        if (userId != null) {
            userIdToChatIdMap.remove(userId);
        }
    }

    public boolean containsChatIdKey(Long chatId) {
        return chatIdToUserIdMap.containsKey(chatId);
    }
    public boolean containsUserIdKey(String userId) {
        return userIdToChatIdMap.containsKey(userId);
    }
}
