package com.example.ftpclient.Logic.Command;

import com.example.ftpclient.Logic.Client.IOManager;
import com.example.ftpclient.Logic.Client.Status;
import com.example.ftpclient.Logic.Transmitter.*;
import com.example.ftpclient.Logic.Util.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Command {
    private static final CommandDealer defaultDealer;       //当命令不存在时调用此处理方式
    private static final HashMap<String, CommandDealer> commands = new HashMap<>();

    static {
        defaultDealer = (commandLine, status, ioManager) -> {
            ioManager.setResponse("invalid command");
            return null;
        };

        commands.put("USER", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            ioManager.setResponse(ioManager.read());
            return null;
        });

        commands.put("PASS", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            ioManager.setResponse(ioManager.read());
            return null;
        });

        commands.put("PASV", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 227) {
                String ip = Tools.parseHost(response);
                int port = Tools.parsePort(response);
                try {
                    ioManager.setDataSocket(new Socket(ip, port));
                    status.setPassive(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response = ioManager.read();
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("PORT", (commandLine, status, ioManager) -> {
            String[] parameters = commandLine.split(" ");
            ServerSocket serverSocket = null;
            try {
                int port;
                if (parameters.length > 1) port = Tools.parsePort(parameters[1]);
                else port = Tools.getFreePort();
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(Constants.CONNECT_TIME_OUT);

                ioManager.write(String.format("PORT %s", Tools.getAddress(port)));
                Socket temp = serverSocket.accept();
                String response = ioManager.read();
                if (Tools.getReturnCode(response) == 200) {
                    ioManager.setDataSocket(temp);
                    ioManager.setServerSocket(serverSocket);
                    status.setPassive(false);
                } else serverSocket.close();
                ioManager.setResponse(response);
            } catch (IOException ignored) {
                try {
                    if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        commands.put("LIST", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();

            if (!(Tools.getReturnCode(response) == 200)) return null;
            else {
                StringBuilder fileList = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(ioManager.getDataSocket().getInputStream()))) {
                    String temp;
                    while ((temp = reader.readLine()) != null) {
                        fileList.append(" ").append(temp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                doCommand(status.isPassive() ? "PASV" : "PORT", status, ioManager);
                ioManager.setResponse(response);

                return fileList.toString();
            }
        });

        commands.put("QUIT", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            ioManager.setResponse(ioManager.read());
            ioManager.close();
            return null;
        });

        commands.put("TYPE", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 200) {
                String[] parameters = commandLine.split(" ");
                if ("I".equalsIgnoreCase(parameters[1])) status.setType(Constants.IMAGE);
                else status.setType(Constants.ASCII);
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("MODE", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 200) {
                String[] parameters = commandLine.split(" ");
                if ("B".equalsIgnoreCase(parameters[1])) status.setMode(Constants.BLOCK);
                else if ("C".equalsIgnoreCase(parameters[1]))
                    status.setMode(Constants.COMPRESSED);
                else status.setMode(Constants.STREAM);
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("STRU", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 200) {
                String[] parameters = commandLine.split(" ");
                if ("R".equalsIgnoreCase(parameters[1])) status.setStructure(Constants.RECORD);
                else if ("P".equalsIgnoreCase(parameters[1]))
                    status.setStructure(Constants.PAGE);
                else status.setStructure(Constants.FILE);
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("ZIP", (commandLine, status, ioManager) -> {
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 200) {
                String[] parameters = commandLine.split(" ");
                status.setZipped("T".equalsIgnoreCase(parameters[1]));
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("RETR", (commandLine, status, ioManager) -> {
            long start = System.currentTimeMillis();
            ioManager.write(commandLine);
            String response = ioManager.read();
            if (Tools.getReturnCode(response) == 150) {
                String[] parameters = commandLine.split(" ");
                String fileName = parameters[1].contains(File.separator) ? parameters[1].substring(parameters[1].lastIndexOf(File.separator) + 1) : parameters[1];
                File file = new File(Constants.BASE_DIR + File.separator + fileName);
                Socket dataSocket = ioManager.getDataSocket();
                Transmitter transmitter = (status.getType() == Constants.ASCII) ? new AsciiTransmitter(file, dataSocket) : new ImageTransmitter(file, dataSocket);
                transmitter.retrieve(status.getStructure());
                response = ioManager.read() + "using " + (System.currentTimeMillis() - start) + " miliseconds";

                //若为File结构，需要重新建立数据连接
                if (status.getStructure() == Constants.FILE || status.getStructure() == Constants.RECORD) {
                    ioManager.closeDataSocket();
                    doCommand(status.isPassive() ? "PASV" : "PORT", status, ioManager);
                }
            }
            ioManager.setResponse(response);
            return null;
        });

        commands.put("STOR", (commandLine, status, ioManager) -> {
            String[] parameters = commandLine.split(" ");
            if (parameters.length < 3) ioManager.setResponse("file name not specified");
            else {
                String uploadName = parameters[1];
                String filePath = parameters[2];

                File file = new File(filePath);
                if (!file.exists() || file.isDirectory())
                    ioManager.setResponse("File not exists or is folder");
                else {
                    ioManager.write("STOR " + uploadName);
                    String response = ioManager.read();
                    if (Tools.getReturnCode(response) == 150) {
                        Socket dataSocket = ioManager.getDataSocket();
                        Transmitter transmitter = (status.getType() == Constants.ASCII) ?
                                new AsciiTransmitter(file, dataSocket) : new ImageTransmitter(file, dataSocket);
                        transmitter.store(status.getStructure());

                        //若为File结构，需要重新建立数据连接
                        if (status.getStructure() == Constants.FILE || status.getStructure() == Constants.RECORD) {
                            ioManager.closeDataSocket();
                            response = ioManager.read();
                            doCommand(status.isPassive() ? "PASV" : "PORT", status, ioManager);
                        } else response = ioManager.read();
                    }

                    ioManager.setResponse(response);
                }
            }
            return null;
        });

        commands.put("NOOP", (commandLine, status, ioManager) -> {
            ioManager.write("NOOP");
            return null;
        });
    }

    public static String doCommand(String commandLine, Status status, IOManager ioManager) {
        String[] parameters = commandLine.split(" ");
        CommandDealer commandDealer = commands.getOrDefault(parameters[0].toUpperCase(), defaultDealer);
        return commandDealer.deal(commandLine, status, ioManager);
    }
}
