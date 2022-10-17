package com.example.ftpclient.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ftpclient.FileAdapter.FileAdapter;
import com.example.ftpclient.FileAdapter.FileItem;
import com.example.ftpclient.FileAdapter.UploadAdapter;
import com.example.ftpclient.Logic.Util.Constants;
import com.example.ftpclient.R;
import com.example.ftpclient.Service.InteractionService;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {
    private final ArrayList<FileItem> fileItems = new ArrayList<>();
    private final FileAdapter fileAdapter = new UploadAdapter(this, fileItems);

    private UploadReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.fileRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileAdapter);

        fileAdapter.setFiles(Constants.BASE_DIR);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.action_upload));
        receiver = new UploadReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "download");
        menu.add(0, 1, 1, "terminal");
        menu.add(0, 2, 2, "settings");
        menu.add(0, 3, 3, "refresh");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 1:
                startActivity(new Intent(this, TerminalActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case 3:
                fileAdapter.setFiles(Constants.BASE_DIR);
                break;
            default:
                startActivity(new Intent(this, DownloadActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class UploadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String information;
            if (!intent.getBooleanExtra(InteractionService.FLAG, false))
                information = "upload failed";
            else
                information = "upload success\nused " + intent.getLongExtra(InteractionService.TIME, -1) + " miliseconds";
            Toast.makeText(context, information, Toast.LENGTH_SHORT).show();
        }
    }
}