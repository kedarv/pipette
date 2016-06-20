package com.kedarv.pipette;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private List<Chat> cList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    Socket socket = null;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    int connectionAttempt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Set up RecyclerView and Adapter for the Conversation List */
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        chatAdapter = new ChatAdapter(cList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(chatAdapter);

        /* Set up socket TODO: Move to thread */
        SocketApplication app = new SocketApplication(getApplicationContext());
        socket = app.getSocket();
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnectAttempt);
        socket.connect();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Chat c = cList.get(position);
                Toast.makeText(getApplicationContext(), c.getChatID() + " is selected!", Toast.LENGTH_SHORT).show();
                socket.emit("getChat", c.getChatID(), new Ack() {
                    @Override
                    public void call(Object... args) {
                        Intent i = new Intent(getApplicationContext(), Messaging.class);
                        i.putExtra("data",args[0].toString());
                        i.putExtra("chat_id", c.getChatID());
                        i.putExtra("guid", c.getGuid());
                        i.putExtra("people", c.getPeopleAsString());
                        Log.d("chat_id", c.getChatID() + "");
                        Log.d("guid", c.getGuid());
                        Log.d("data", args[0].toString());
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.i("longPress", "longPress");
            }
        }));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            fetchConversations();
        }
    }

    public void fetchConversations() {
        socket.emit("getAllChat", new Ack() {
            @Override
            public void call(Object... args) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    // Establish root node
                    JsonNode rootNode = mapper.readTree(args[0].toString());
                    Iterator<JsonNode> iterator = rootNode.elements();
                    while (iterator.hasNext()) {
                        HashMap<String, String> peopleMap = new HashMap<String, String>();
                        JsonNode chatThread = iterator.next();
                        long date = chatThread.get("lastUpdate").asLong();
                        Iterator<JsonNode> peopleIterator = chatThread.get("people").elements();
                        while(peopleIterator.hasNext()) {
                            JsonNode person = peopleIterator.next();
                            String number = person.get("value").toString().replace("\"", "");
                            if(!number.contains("@")) {
                                number = String.format("(%s) %s-%s", number.substring(2, 5), number.substring(5, 8),
                                        number.substring(8, 12));
                            }
                            String name = getContactName(getApplicationContext(), number);
                            if (name != null) {
                                peopleMap.put(number, name);
                            } else {
                                peopleMap.put(number, number);
                            }
                        }
                        Chat chat = new Chat(chatThread.get("chat_id").asInt(), peopleMap, date, chatThread.get("guid").asText());
                        cList.add(chat);
                        Log.w("people", peopleMap.toString());
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
        chatAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchConversations();
            } else {
                Toast.makeText(this, "Can't display contact names until permission is granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    private Emitter.Listener onReconnectAttempt = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            connectionAttempt++;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(connectionAttempt < 5) {
                        Toast.makeText(getApplicationContext(),
                                "Could not connect to server, retrying with attempt #" + connectionAttempt, Toast.LENGTH_LONG).show();
                    }
                    else {
                        socket.disconnect();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                        builder.setTitle("Couldn't connect to Server");
                        builder.setMessage("Please check your settings and ensure the server is running.");
                        builder.setPositiveButton("OK", null);
                        builder.show();

                    }
                }
            });
        }
    };
}
