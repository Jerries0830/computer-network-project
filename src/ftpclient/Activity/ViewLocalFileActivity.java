package com.example.ftpclient.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.ftpclient.FileAdapter.FileAdapter;
import com.example.ftpclient.FileAdapter.FileItem;
import com.example.ftpclient.FileAdapter.LocalAdapter;
import com.example.ftpclient.Logic.Util.Constants;
import com.example.ftpclient.R;

import java.util.ArrayList;

public class ViewLocalFileActivity extends AppCompatActivity {
    private final ArrayList<FileItem> fileItems = new ArrayList<>();
    private final FileAdapter fileAdapter = new LocalAdapter(this, fileItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_file);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.fileRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileAdapter);

        fileAdapter.setFiles(Constants.BASE_DIR);
    }
}