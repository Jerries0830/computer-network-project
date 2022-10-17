package com.example.ftpclient.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.ftpclient.Logic.Util.Tools;
import com.example.ftpclient.R;

public class FileInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_information);

        TextView size = findViewById(R.id.information_size);
        TextView md5 = findViewById(R.id.information_md5);

        String path = getIntent().getStringExtra("path");
        size.setText(String.valueOf(Tools.getSize(path)));
        md5.setText(Tools.getMD5(path));
    }
}