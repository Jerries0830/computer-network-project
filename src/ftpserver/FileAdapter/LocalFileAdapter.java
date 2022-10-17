package com.example.ftpserver.FileAdapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.ftpserver.Activity.FileInformationActivity;
import com.example.ftpserver.MyApplication;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class LocalFileAdapter extends FileAdapter{
    private final Context context;

    public LocalFileAdapter(Context context, List<FileItem> fileItemList) {
        super(fileItemList);
        this.context = context;
    }

    @Override
    public void setFiles(String dirPath) {
        fileItemList.clear();
        File dir = new File(dirPath);
        if (!dirPath.equals(MyApplication.getContext().getFilesDir().getPath()))
            fileItemList.add(new FileItem(dirPath, "../"));
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            fileItemList.add(new FileItem(dirPath, file.getName()));
        }
        this.notifyDataSetChanged();
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
