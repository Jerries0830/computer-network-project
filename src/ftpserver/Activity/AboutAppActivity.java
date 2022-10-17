package com.example.ftpserver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ftpserver.Logic.Util.Constants;
import com.example.ftpserver.Logic.Util.Tools;
import com.example.ftpserver.R;

public class AboutAppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        TextView ip = findViewById(R.id.about_ip);
        ip.setText(Tools.getIP());

        TextView port = findViewById(R.id.about_port);
        port.setText(String.valueOf(Constants.LISTEN_PORT));
    }
}