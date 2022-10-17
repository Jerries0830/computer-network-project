package com.example.ftpserver.Logic.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class IOManager {
    private BufferedReader reader = null;   //读取命令
    private PrintWriter writer = null;      //返回响应

    private Socket commandSocket;
    private Socket dataSocket;
    private ServerSocket serverSocket;      //如果主动接收datasocket连接请求，需要在其关闭时同时关闭，否则端口会被一直占用

    public void setCommandSocket(Socket socket) {
        commandSocket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(commandSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDataSocket(Socket dataSocket) {
        if (this.dataSocket != null && !this.dataSocket.isClosed()) closeDataSocket();
        this.dataSocket = dataSocket;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public void closeDataSocket() {
        if (dataSocket != null && !dataSocket.isClosed()) {
            try {
                dataSocket.shutdownOutput();
                dataSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean isDataSocketAvailable() {
        return dataSocket != null && !dataSocket.isClosed();
    }

    public void write(String content) {
        writer.println(content);
        writer.flush();
    }

    public String read() throws IOException {
        return reader.readLine().trim();
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            closeDataSocket();
            if (commandSocket != null && !commandSocket.isClosed()) commandSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
