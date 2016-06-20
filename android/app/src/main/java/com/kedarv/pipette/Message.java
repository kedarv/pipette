package com.kedarv.pipette;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by kedar on 6/16/16.
 */
public class Message {
    private String text;
    private String sender;
    private int from_me;
    private int chat_id;
    private int date;
    private int status;

    public Message(String text, String sender, int from_me, int chat_id, int date, int status) {
        this.text = text;
        this.sender = sender;
        this.from_me = from_me;
        this.chat_id = chat_id;
        this.date = date;
        this.status = status;
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
    public int getStatus() {
        return status;
    }
    public void setStatus(int s) {
        status = s;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                        append(text).
                        append(sender).
                        append(from_me).
                        append(chat_id).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message))
            return false;
        if (obj == this)
            return true;

        Message m = (Message) obj;

        return new EqualsBuilder().
                        append(text, m.text).
                        append(sender, m.sender).
                        append(from_me, m.from_me).
                        append(chat_id, m.chat_id).
                        isEquals();
    }
}
