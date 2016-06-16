package com.kedarv.pipette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private List<Chat> cList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        chatAdapter = new ChatAdapter(cList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(chatAdapter);


        try {
            String URL = "http://" + prefs.getString("server_ip", "http://127.0.0.1/") + ":" + prefs.getString("server_port", "3000");
            socket = IO.socket(URL);
            Log.w("URL", URL);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Chat c = cList.get(position);
                Toast.makeText(getApplicationContext(), c.getChatID() + " is selected!", Toast.LENGTH_SHORT).show();
                socket.emit("getChat", c.getChatID(), new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.w("pls work", args[0].toString());
                        Intent i = new Intent(getApplicationContext(), Messaging.class);
                        i.putExtra("data",args[0].toString());
                        i.putExtra("people", c.getPeopleAsString());
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
                            String number = person.get("value").toString().replace("\"", "");
                            number = String.format("(%s) %s-%s", number.substring(2, 5), number.substring(5, 8),
                                    number.substring(8, 12));
                            p.put(number, person.get("lookupValue").toString());
                        }
                        Chat chat = new Chat(chat_id, p, date);
                        cList.add(chat);
                        Log.w("people", p.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
//        HashMap<String, String> s = new HashMap<>();
//        s.put("lol", "lol");
//        Chat c = new Chat(10, s, 10);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);
//        cList.add(c);

        chatAdapter.notifyDataSetChanged();
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                Log.w("click", "click");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
