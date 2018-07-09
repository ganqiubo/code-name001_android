package com.pojul.fastIM.message.chat;

public class DateMessage extends ChatMessage{

    private String date;

    public DateMessage(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
