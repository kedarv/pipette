package com.kedarv.pipette;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Messaging extends AppCompatActivity {
    Socket socket = null;
    SharedPreferences prefs;
    String guid;
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private EditText sendMessageField;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(extras.getString("people"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Set up RecyclerView and Adapter for the Message List */
        recyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        mAdapter = new MessageAdapter(messageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        /* Set up Socket TODO: Move to Thread */
        SocketApplication app = new SocketApplication(getApplicationContext());
        socket = app.getSocket();
        socket.connect();

        final ObjectMapper mapper = new ObjectMapper();
        String data = extras.getString("data");
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert rootNode.elements() != null;
        Iterator<JsonNode> iterator = rootNode.elements();
        while (iterator.hasNext()) {
            JsonNode messages = iterator.next();
            guid = messages.get("guid").asText();
            Message m = new Message(messages.get("text").asText(), messages.get("who_from").asText(), messages.get("is_from_me").asInt(), messages.get("chat_id").asInt(), messages.get("date").asInt());
            messageList.add(m);
            Log.w("message", m.toString());
        }
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);

        sendButton = (Button) findViewById(R.id.sendBtn);
        sendMessageField = (EditText) findViewById(R.id.msgText);
        final SmoothProgressBar progressBar = (SmoothProgressBar) findViewById(R.id.progress);

        assert sendButton != null;
        assert sendMessageField != null;
        assert progressBar != null;

        sendButton.setEnabled(false);
        // this MUST be put on another thread to reduce UI lag
        socket.on("updateChatData", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JsonNode newNode = null;
                try {
                    newNode = mapper.readTree(args[0].toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.w("here!", newNode.toString());
                JsonNode node = newNode.get("data").get(0);
                final Message m = new Message(node.get("text").asText(), node.get("who_from").asText(), node.get("is_from_me").asInt(), node.get("chat_id").asInt(), node.get("date").asInt());
                messageList.add(m);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(m.getFrom_me() == 1) {
                            sendMessageField.setVisibility(View.VISIBLE);
                            sendButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        mAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    }});
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    send(sendMessageField.getText().toString(), guid);
                    sendMessageField.setText("");
                    sendButton.setVisibility(View.INVISIBLE);
                    sendMessageField.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendMessageField.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(sendMessageField.getText().toString().length() > 0) {
                    sendButton.setEnabled(true);
                }
                else {
                    sendButton.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void send(String msg, String guid) throws URISyntaxException, JSONException {
        JSONObject json = new JSONObject();
        json.put("text",  msg);
        json.put("guid", guid);

        socket.emit("sendMessage", json, new Ack() {
            @Override
            public void call(Object... args) {
                Log.w("sending message", args[0].toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button press
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close activity and go back to conversation view
        }
        return super.onOptionsItemSelected(item);
    }

}
