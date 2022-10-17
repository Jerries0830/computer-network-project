package com.example.ftpclient.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ftpclient.Service.InteractionService;
import com.example.ftpclient.R;

public class ConnectActivity extends AppCompatActivity {
    private EditText serverIP;
    private EditText serverPort;
    private EditText editUser;
    private EditText editPassword;
    private CheckBox anonymous;
    private CheckBox rememberServer;
    private CheckBox rememberUser;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView rememberUserTextView;
    private Button connect;

    private SharedPreferences.Editor editor;
    private ConnectReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initContent();

        anonymous.setOnCheckedChangeListener((view, isChecked) -> setAnonymousUI(isChecked));
        connect.setOnClickListener(view -> connect());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.action_connect));
        receiver = new ConnectReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        InteractionService.doQuit(this);
    }

    private void initContent() {
        serverIP = findViewById(R.id.serverIP);
        serverPort = findViewById(R.id.serverPort);
        editUser = findViewById(R.id.user);
        editPassword = findViewById(R.id.password);
        anonymous = findViewById(R.id.anonymous);
        rememberServer = findViewById(R.id.rememberServer);
        rememberUser = findViewById(R.id.rememberUser);
        rememberUserTextView = findViewById(R.id.rememeberUserTextView);
        progressBar = findViewById(R.id.connectProgressBar);
        errorMessage = findViewById(R.id.connectErrorMessage);
        connect = findViewById(R.id.connect);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("rememberServer", false)) {
            serverIP.setText(sharedPreferences.getString("serverIP", ""));
            serverPort.setText(sharedPreferences.getString("serverPort", ""));
            rememberServer.setChecked(true);
        }

        if (sharedPreferences.getBoolean("rememberUser", false)) {
            editUser.setText(sharedPreferences.getString("user", ""));
            editPassword.setText(sharedPreferences.getString("password", ""));
            rememberUser.setChecked(true);
        }
    }

    private void setAnonymousUI(boolean isChecked) {
        if (isChecked) {
            editUser.setVisibility(View.INVISIBLE);
            editPassword.setVisibility(View.INVISIBLE);
            rememberUser.setVisibility(View.INVISIBLE);
            rememberUserTextView.setVisibility(View.INVISIBLE);
        } else {
            editUser.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
            rememberUser.setVisibility(View.VISIBLE);
            rememberUserTextView.setVisibility(View.VISIBLE);
        }
    }

    private void connect() {
        errorMessage.setVisibility(View.INVISIBLE);
        if (!anonymous.isChecked() && (serverIP.getText().toString().equals("") || serverPort.getText().toString().equals("")
                || editUser.getText().toString().equals("") || editPassword.getText().toString().equals(""))) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("please fill information first");
        } else {
            connect.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);

            String host = serverIP.getText().toString();
            String port = serverPort.getText().toString();
            String user = anonymous.isChecked() ? "anonymous" : editUser.getText().toString();
            String pass = anonymous.isChecked() ? "" : editPassword.getText().toString();
            InteractionService.doConnect(this, host, port, user, pass);
        }
    }

    private class ConnectReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            connect.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);

            switch (intent.getIntExtra(InteractionService.CONNECT_STATUS, -1)) {
                case -1:
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("failed to connect to the server");
                    break;
                case 0:
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("username or password incorrect");
                    break;
                default:
                    if (rememberServer.isChecked()) {
                        editor.putBoolean("rememberServer", true);
                        editor.putString("serverIP", serverIP.getText().toString());
                        editor.putString("serverPort", serverPort.getText().toString());
                    } else {
                        editor.remove("rememberServer");
                        editor.remove("serverIP");
                        editor.remove("serverPort");
                    }

                    if (!anonymous.isChecked() && rememberUser.isChecked()) {
                        editor.putBoolean("rememberUser", true);
                        editor.putString("user", editUser.getText().toString());
                        editor.putString("password", editPassword.getText().toString());
                    } else {
                        editor.remove("rememberUser");
                        editor.remove("user");
                        editor.remove("password");
                    }
                    editor.apply();
                    Intent newIntent = new Intent(context, DownloadActivity.class);
                    startActivity(newIntent);
            }
        }
    }
}