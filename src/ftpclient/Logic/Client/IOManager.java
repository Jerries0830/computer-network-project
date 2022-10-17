package com.example.ftpclient.Logic.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class IOManager {
    private BufferedReader reader = null;   //读取响应
    private PrintWriter writer = null;      //发送命令

    private String response = "";           //记录上次命令执行的响应

    private Socket commandSocket;
    private Socket dataSocket;
    private ServerSocket serverSocket;      //如果以accept方式创建datasocket，需要同时关闭，否则端口会被一直占用

    public void setCommandSocket(Socket socket) {
        commandSocket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(commandSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return commandSocket != null && !commandSocket.isClosed();
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

    public void setServerSocket(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void write(String content) {
        writer.println(content);
        writer.flush();
    }

    public String read() {
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void close() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (dataSocket != null && !dataSocket.isClosed()) closeDataSocket();
            if (commandSocket != null && !commandSocket.isClosed()) commandSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
