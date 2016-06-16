package com.kedarv.pipette;

/**
 * Created by kedar on 6/16/16.
 */
public class Message {
    private String text;
    private String sender;
    private int from_me;
    private int chat_id;
    private int date;

    public Message(String text, String sender, int from_me, int chat_id, int date) {
        this.text = text;
        this.sender = sender;
        this.from_me = from_me;
        this.chat_id = chat_id;
        this.date = date;
    }

    public String getText() {
        return text;
    }
    public String getSender() {
        return sender;
    }
    public int getFrom_me() {
        return from_me;
    }
    public int getChat_id() {
        return chat_id;
    }
    public int getDate() {
        return date;
    }

}
