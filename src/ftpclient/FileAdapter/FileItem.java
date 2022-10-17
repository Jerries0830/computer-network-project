package com.example.ftpclient.FileAdapter;

import com.example.ftpclient.R;

import java.io.File;

public class FileItem {
    protected final String directory;
    protected final String name;

    public FileItem(String directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public String getPath(){
        return directory + File.separator + name;
    }

    public int getImageID() {
        if (isFile()) return R.mipmap.file;
        else return R.mipmap.folder;
    }

    public boolean isFile() {
        File file = new File(directory + File.separator + name);
        return file.isFile();
    }
}