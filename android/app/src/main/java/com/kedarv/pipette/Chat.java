package com.kedarv.pipette;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by kedar on 6/15/16.
 */
public class Chat {
    private HashMap<String, String> people;
    private long date;
    private int chat_id;
    private String guid;

    public Chat(int chat_id, HashMap<String, String> people, long date, String guid) {
        this.chat_id = chat_id;
        this.people = people;
        this.date = date;
        this.guid = guid;
    }
    public String toString() {
        return "chat_id: " + chat_id + " people: " + people.toString() + " date: " + date;
    }

    public HashMap<String, String> getPeople() {
        return people;
    }
    public String getPeopleAsString() {
        StringBuilder sb = new StringBuilder();

        for (String key : people.values()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(key);
        }
        return sb.toString();
    }
    public long getDate() {
        return date;
    }
    public String getReadableDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, h:mm a");
        return sdf.format(date * 1000L);
    }
    public int getChatID() {
        return chat_id;
    }
    public String getGuid() {return guid; }
}
