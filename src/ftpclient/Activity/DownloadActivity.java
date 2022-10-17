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

import com.example.ftpclient.FileAdapter.DownloadAdapter;
import com.example.ftpclient.FileAdapter.FileAdapter;
import com.example.ftpclient.FileAdapter.FileItem;
import com.example.ftpclient.R;
import com.example.ftpclient.Service.InteractionService;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {
    private final ArrayList<FileItem> fileItems = new ArrayList<>();
    private final FileAdapter fileAdapter = new DownloadAdapter(this, fileItems);

    private DownloadReceiver downloadReceiver;
    private ListReceiver listReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.fileRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileAdapter);

        IntentFilter downloadFilter = new IntentFilter();
        downloadFilter.addAction(getString(R.string.action_download));
        downloadReceiver = new DownloadReceiver();
        registerReceiver(downloadReceiver, downloadFilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.action_list));
        listReceiver = new ListReceiver();
        registerReceiver(listReceiver, filter);

        InteractionService.doList(this, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
        unregisterReceiver(listReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "upload");
        menu.add(0, 1, 1, "terminal");
        menu.add(0, 2, 2, "settings");
        menu.add(0, 3, 3, "refresh");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                startActivity(new Intent(this, TerminalActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case 3:
                InteractionService.doList(this, "");
                break;
            default:
                startActivity(new Intent(this, UploadActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String information;
            if (!intent.getBooleanExtra("flag", false)) information = "download failed";
            else
                information = "download success\nused " + intent.getLongExtra("time", -1) + " miliseconds";
            Toast.makeText(context, information, Toast.LENGTH_SHORT).show();
        }
    }

    private class ListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String dirPath = intent.getStringExtra(InteractionService.DIR);
            String files = intent.getStringExtra(InteractionService.FILES);
            fileAdapter.setFiles(dirPath, files);
        }
    }
}