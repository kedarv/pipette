package com.kedarv.pipette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

public class Messaging extends AppCompatActivity {
    Socket socket = null;
    SharedPreferences prefs;
    String guid;
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        recyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        mAdapter = new MessageAdapter(messageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("data");


            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            Iterator<JsonNode> iterator = rootNode.elements();
            while (iterator.hasNext()) {
                JsonNode messages = iterator.next();
                guid = messages.get("guid").asText();
                Log.w("guid", guid);
                if(messages.get("is_from_me").asInt() == 1) {
                    sb.append("from me: ");
                }
               sb.append(messages.get("text") + "\n");
                Message m = new Message(messages.get("text").asText(), messages.get("who_from").asText(), messages.get("is_from_me").asInt(), messages.get("chat_id").asInt(), messages.get("date").asInt());
                messageList.add(m);
            }
            mAdapter.notifyDataSetChanged();
        }

        Button button= (Button) findViewById(R.id.sendBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tv = (EditText) findViewById(R.id.msgText);
                try {
                    Log.w("click", "see click");
                    send(tv.getText().toString(), guid);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void send(String msg, String guid) throws URISyntaxException {
        String URL = "http://" + prefs.getString("server_ip", "http://127.0.0.1/") + ":" + prefs.getString("server_port", "3000");
        socket = IO.socket(URL);
        socket.connect();
        JSONObject json = new JSONObject();
        try {
            json.put("text",  msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            json.put("guid", guid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("sendMessage", json, new Ack() {
            @Override
            public void call(Object... args) {
                Log.w("pls work", args[0].toString());
            }
        });
    }
}
