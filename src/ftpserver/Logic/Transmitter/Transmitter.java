package com.example.ftpserver.Logic.Transmitter;

import java.io.File;
import java.net.Socket;

public abstract class Transmitter {
    File file;
    Socket socket;

    public Transmitter(File file, Socket socket) {
        this.file = file;
        this.socket = socket;
    }

    public abstract boolean retrieve(int structure);

    public abstract boolean store(int structure);
}
