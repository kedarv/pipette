package com.kedarv.pipette;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by kedar on 6/17/16.
 */
public class SocketApplication {
    private Socket socket;
    private static String URL;
    SocketApplication(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            URL = "http://" + prefs.getString("server_ip", "http://127.0.0.1/") + ":" + prefs.getString("server_port", "3000");
            socket = IO.socket(URL);
        } catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getSocket() {
        return socket;
    }
    public static String getSocketURL() {return URL; }
}
