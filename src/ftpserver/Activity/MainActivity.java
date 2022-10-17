package com.example.ftpserver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ftpserver.R;
import com.example.ftpserver.Service.ListenService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchCompat listenSwitch = findViewById(R.id.statusSwitch);
        listenSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                listenSwitch.setText("Status: On");
                Intent intent = new Intent(this, ListenService.class);
                startService(intent);
            } else {
                listenSwitch.setText("Status: Off");
                Intent intent = new Intent(this, ListenService.class);
                stopService(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "view files");
        menu.add(0, 1, 1, "about");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            Intent intent = new Intent(this, ViewLocalFileActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == 1){
            Intent intent = new Intent(this, AboutAppActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}