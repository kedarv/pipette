package com.kedarv.pipette;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    String URL = "http://10.186.107.70:3000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Socket socket = null;
        try {
            socket = IO.socket(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        socket.emit("getAllChat", new Ack() {
            @Override
            public void call(Object... args) {
                ObjectMapper mapper = new ObjectMapper();

                try {
                    // Establish root node
                    JsonNode rootNode = mapper.readTree(args[0].toString());
                    Iterator<JsonNode> iterator = rootNode.elements();
                    while (iterator.hasNext()) {
                        HashMap<String, String> p = new HashMap<String, String>();
                        JsonNode chatThread = iterator.next();
                        int chat_id = chatThread.get("chat_id").asInt();
                        long date = chatThread.get("lastUpdate").asLong();
                        Iterator<JsonNode> peopleIterator = chatThread.get("people").elements();
                        while(peopleIterator.hasNext()) {
                            JsonNode person = peopleIterator.next();
                            p.put(person.get("value").toString(), person.get("lookupValue").toString());
                        }
                        Chat chat = new Chat(chat_id, p, date);
                        Log.w("chat", chat.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        setContentView(R.layout.activity_main);
    }
}
