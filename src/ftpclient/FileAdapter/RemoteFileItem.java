package com.example.ftpclient.FileAdapter;

import java.io.File;

public class RemoteFileItem extends FileItem {
    private final boolean isFile;

    public RemoteFileItem(String directory, String name, boolean isFile) {
        super(directory, name);
        this.isFile = isFile;
    }

    @Override
    public String getPath() {
        if(directory.equals("")) return name;
        else return directory + File.separator + name;
    }

    @Override
    public boolean isFile() {
        return isFile;
    }
}
