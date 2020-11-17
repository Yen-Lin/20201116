package com.koddev.authenticatorapp.Chat;

public class ChatMessage {
    private boolean isImage, isMine;
    private String content;
    private String mDateTime;

    public ChatMessage(String message,String dateTime, boolean mine, boolean image) {
        content = message;
        isMine = mine;
        isImage = image;
        mDateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String dateTime()
    {
        return mDateTime;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
}
