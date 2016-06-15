package com.kedarv.pipette;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Iterator;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

public class Messaging extends AppCompatActivity {
    Socket socket = null;
    SharedPreferences prefs;
    String guid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("data");
            TextView t=(TextView)findViewById(R.id.convo);

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
            }
            t.setText(sb.toString());
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
