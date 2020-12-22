package com.example.whatsapps;

public class MessageDetails
{
    String from,type,text,to,messageID,time,date;

    public MessageDetails()
    {

    }
    public MessageDetails(String from, String type, String text, String to, String messageID, String time, String date) {
        this.from = from;
        this.type = type;
        this.text = text;
        this.to = to;
        this.messageID = messageID;
        this.time = time;
        this.date = date;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
