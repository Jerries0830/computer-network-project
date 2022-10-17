package com.example.ftpclient.FileAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.ftpclient.Activity.FileInformationActivity;
import com.example.ftpclient.MyApplication;
import com.example.ftpclient.Service.InteractionService;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class UploadAdapter extends FileAdapter {
    public UploadAdapter(Context context, List<FileItem> fileItemList) {
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

        holder.itemView.setOnLongClickListener(view -> {
            if (!fileItem.getName().equals("../")) {
                AlertDialog.Builder uploadDialog = new AlertDialog.Builder(context);
                uploadDialog.setTitle("Upload");
                uploadDialog.setMessage("Are you sure to upload " + fileItem.getName() + " ?");
                uploadDialog.setPositiveButton("Yes", (dialog, i) -> {
                    InteractionService.doUpload(context, fileItem.getName(), fileItem.getPath());
                });
                uploadDialog.show();
            }
            return true;
        });
    }
}
