package com.example.ftpclient.FileAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftpclient.Logic.Util.Constants;
import com.example.ftpclient.MyApplication;
import com.example.ftpclient.R;

import java.io.File;
import java.util.List;
import java.util.Objects;

public abstract class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileHolder> {
    protected final List<FileItem> fileItemList;
    protected final Context context;

    public FileAdapter(Context context, List<FileItem> fileItemList) {
        this.context = context;
        this.fileItemList = fileItemList;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        FileItem fileItem = fileItemList.get(position);
        holder.fileName.setText(fileItem.getName());
        holder.fileIcon.setImageResource(fileItem.getImageID());
    }

    @Override
    public int getItemCount() {
        return fileItemList.size();
    }

    public void setFiles(String dirPath) {
        fileItemList.clear();
        if (!dirPath.equals(Constants.BASE_DIR))
            fileItemList.add(new FileItem(dirPath, "../"));

        File dir = new File(dirPath);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            fileItemList.add(new FileItem(dirPath, file.getName()));
        }

        this.notifyDataSetChanged();
    }

    public void setFiles(String dirPath, String files) {
        fileItemList.clear();
        if (!dirPath.equals("")) fileItemList.add(new FileItem(dirPath, "../"));

        if(!files.equals("")) {
            String[] temp = files.split(" ");
            for (int i = 0; i < temp.length; i += 2) {
                String fileName = temp[i];
                boolean isFile = temp[i + 1].equals("0");
                fileItemList.add(new RemoteFileItem(dirPath, fileName, isFile));
            }
        }

        this.notifyDataSetChanged();
    }

    protected static class FileHolder extends RecyclerView.ViewHolder {
        private final ImageView fileIcon;
        private final TextView fileName;

        public FileHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}