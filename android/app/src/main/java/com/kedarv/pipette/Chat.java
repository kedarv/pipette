package com.kedarv.pipette;

import java.util.HashMap;

/**
 * Created by kedar on 6/15/16.
 */
public class Chat {
    HashMap<String, String> people;
    long date;
    int chat_id;

    public Chat(int chat_id, HashMap<String, String> people, long date) {
        this.chat_id = chat_id;
        this.people = people;
        this.date = date;
    }
    public String toString() {
        return "chat_id: " + chat_id + " people: " + people.toString() + " date: " + date;
    }
}
