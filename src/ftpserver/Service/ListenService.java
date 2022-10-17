package com.example.ftpserver.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ftpserver.Activity.MainActivity;
import com.example.ftpserver.Logic.Command.Command;
import com.example.ftpserver.Logic.Server.IOManager;
import com.example.ftpserver.Logic.Server.Status;
import com.example.ftpserver.Logic.Util.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ListenService extends IntentService {
    private ServerSocket serverSocket;

    public ListenService() {
        super("service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_service", "notification", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this, "my_service")
                .setContentTitle("notification")
                .setContentText("server socket is listening")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        try {
            serverSocket = new ServerSocket(Constants.LISTEN_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Socket commandSocket = serverSocket.accept();
                new ConnectThread(commandSocket).start();
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onDestroy() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private static class ConnectThread extends Thread {
        private final Status status = new Status();
        private final IOManager ioManager = new IOManager();

        ConnectThread(Socket commandSocket) {
            ioManager.setCommandSocket(commandSocket);
        }

        @Override
        public void run() {
            try {
                while (!status.isStop()) {
                    String command = ioManager.read();
                    Command.doCommand(command, status, ioManager);
                }
            } catch (IOException | NullPointerException ignored) {
            }
        }
    }
}