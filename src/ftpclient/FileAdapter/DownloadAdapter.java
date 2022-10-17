package com.example.ftpclient.FileAdapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.ftpclient.Service.InteractionService;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends FileAdapter {
    public DownloadAdapter(Context context, List<FileItem> fileItemList) {
        super(context, fileItemList);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        FileItem fileItem = fileItemList.get(position);

        holder.itemView.setOnClickListener(view -> {
            if (!fileItem.isFile()) {
                String filePath;
                if (fileItem.getName().equals("../")) {
                    String dirPath = fileItem.getDirectory();
                    filePath = dirPath.contains(File.separator) ? dirPath.substring(0, dirPath.lastIndexOf(File.separator)) : "";
                } else filePath = fileItem.getPath();
                InteractionService.doList(context, filePath);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (!fileItem.getName().equals("../")) {
                AlertDialog.Builder uploadDialog = new AlertDialog.Builder(context);
                uploadDialog.setTitle("Download");
                uploadDialog.setMessage("Are you sure to download " + fileItem.getName() + " ?");
                uploadDialog.setPositiveButton("Yes", (dialog, i) -> InteractionService.doDownload(context, fileItem.getPath(), fileItem.isFile()));
                uploadDialog.show();
            }
            return true;
        });
    }
}
