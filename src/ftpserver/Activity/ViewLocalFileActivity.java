package com.example.ftpserver.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.ftpserver.FileAdapter.FileAdapter;
import com.example.ftpserver.FileAdapter.FileItem;
import com.example.ftpserver.FileAdapter.LocalFileAdapter;
import com.example.ftpserver.Logic.Util.Constants;
import com.example.ftpserver.R;

import java.util.ArrayList;

public class ViewLocalFileActivity extends AppCompatActivity {
    private final ArrayList<FileItem> fileItems = new ArrayList<>();
    private final FileAdapter fileAdapter = new LocalFileAdapter(this, fileItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_local_file);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(fileAdapter);

        fileAdapter.setFiles(Constants.BASE_DIR);
    }
}