package uz.devops.service.dto;

public class MessageDTo {

    private Long chatId;
    private Integer messageId;

    public MessageDTo(Long chatId, Integer messageId) {
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
}
