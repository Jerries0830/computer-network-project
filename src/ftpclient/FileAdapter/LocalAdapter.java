package com.example.ftpclient.FileAdapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.ftpclient.Activity.FileInformationActivity;

import java.io.File;
import java.util.List;

public class LocalAdapter extends FileAdapter {
    public LocalAdapter(Context context, List<FileItem> fileItemList) {
        super(context, fileItemList);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        FileItem fileItem = fileItemList.get(position);

        holder.itemView.setOnClickListener(view -> {
            if (!fileItem.isFile()) {
                if (fileItem.getName().equals("../"))
                    setFiles(fileItem.getDirectory().substring(0, fileItem.getDirectory().lastIndexOf(File.separator)));
                else setFiles(fileItem.getPath());
            } else {
                Intent intent = new Intent(context, FileInformationActivity.class);
                intent.putExtra("path", fileItem.getPath());
                context.startActivity(intent);
            }
        });
    }
}
