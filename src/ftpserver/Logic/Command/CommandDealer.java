package com.example.ftpserver.Logic.Command;

import com.example.ftpserver.Logic.Server.IOManager;
import com.example.ftpserver.Logic.Server.Status;

public interface CommandDealer {
    void deal(Status status, String[] parameters, IOManager ioManager);
}
