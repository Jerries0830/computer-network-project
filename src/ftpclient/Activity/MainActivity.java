package com.example.ftpclient.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.ftpclient.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = findViewById(R.id.mainConnect);
        connect.setOnClickListener(view -> {
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivity(intent);
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
        } else if (item.getItemId() == 1) {
            Intent intent = new Intent(this, AboutAppActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}