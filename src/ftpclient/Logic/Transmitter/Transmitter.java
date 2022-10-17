package com.example.ftpclient.Logic.Transmitter;

import java.io.File;
import java.net.Socket;

public abstract class Transmitter {
    File file;
    Socket socket;

    public Transmitter(File file, Socket socket) {
        this.file = file;
        this.socket = socket;
    }

    public abstract void retrieve(int structure);

    public abstract void store(int structure);
}
