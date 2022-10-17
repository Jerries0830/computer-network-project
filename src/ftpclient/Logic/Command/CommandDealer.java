package com.example.ftpclient.Logic.Command;

import com.example.ftpclient.Logic.Client.IOManager;
import com.example.ftpclient.Logic.Client.Status;

public interface CommandDealer {
    String deal(String commandLine, Status status, IOManager ioManager);
}
