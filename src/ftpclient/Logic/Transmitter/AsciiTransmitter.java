package com.example.ftpclient.Logic.Transmitter;

import com.example.ftpclient.Logic.Util.Constants;

import java.io.*;
import java.net.Socket;

public class AsciiTransmitter extends Transmitter {
    public AsciiTransmitter(File file, Socket socket) {
        super(file, socket);
    }

    @Override
    public void retrieve(int structure) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new PrintWriter(new FileOutputStream(file)));

            if (structure == Constants.FILE) dealFile(reader, writer);
            else if (structure == Constants.RECORD) dealRecord(reader, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(int structure) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            PrintWriter writer = new PrintWriter(new PrintWriter(socket.getOutputStream()));

            if (structure == Constants.FILE) dealFile(reader, writer);
            else if (structure == Constants.RECORD) dealRecord(reader, writer);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dealFile(BufferedReader reader, PrintWriter writer) {
        try {
            char[] buf = new char[1024];
            int length = reader.read(buf, 0, buf.length);
            while (length != -1) {
                writer.write(buf, 0, length);
                writer.flush();
                length = reader.read(buf, 0, buf.length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dealRecord(BufferedReader reader, PrintWriter writer) {
        try {
            String line = reader.readLine();
            while (line != null) {
                writer.write(line + "\n");
                writer.flush();
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
