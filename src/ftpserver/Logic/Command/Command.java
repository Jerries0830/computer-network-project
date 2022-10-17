package com.example.ftpserver.Logic.Command;

import com.example.ftpserver.Logic.Server.IOManager;
import com.example.ftpserver.Logic.Server.Status;
import com.example.ftpserver.Logic.Util.*;
import com.example.ftpserver.Logic.Transmitter.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Objects;

public class Command {
    private static final CommandDealer defaultDealer;       //当命令不存在时调用此处理方式
    private static final HashMap<String, CommandDealer> commands = new HashMap<>();

    static {
        defaultDealer = (status, parameters, ioManager) -> {
            ioManager.write(ReturnCode.get(500));
        };

        commands.put("USER", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else {
                String username = parameters[1];
                status.setLogged(false);
                status.setUsername(parameters[1]);
                if (username.equals("anonymous")) {
                    status.setLogged(true);
                    ioManager.write(ReturnCode.get(230));
                } else {
                    ioManager.write(ReturnCode.get(331));
                }
            }
        });

        commands.put("PASS", (status, parameters, ioManager) -> {
            if (status.getUsername() == null) ioManager.write(ReturnCode.get(332));
            else {
                String password = parameters.length < 2 ? "" : parameters[1];
                if (!Tools.verify(status.getUsername(), password))
                    ioManager.write(ReturnCode.get(530));
                else {
                    status.setLogged(true);
                    ioManager.write(ReturnCode.get(230));
                }
            }
        });

        commands.put("PASV", (status, parameters, ioManager) -> {
            if (!status.isLogged()) ioManager.write(ReturnCode.get(530));
            else {
                ServerSocket serverSocket = null;
                try {
                    int listenPort = Tools.getFreePort();
                    if (listenPort == -1)
                        ioManager.write(String.format(ReturnCode.get(400), "400 can't open listen port"));
                    else {
                        int p1 = listenPort / 256;
                        int p2 = listenPort % 256;
                        String[] IPArray = Tools.getIPArray();
                        ioManager.write(String.format(ReturnCode.get(227), IPArray[0], IPArray[1], IPArray[2], IPArray[3], p1, p2));

                        serverSocket = new ServerSocket(listenPort);
                        serverSocket.setSoTimeout(Constants.CONNECT_TIME_OUT);
                        ioManager.setDataSocket(serverSocket.accept());
                        ioManager.setServerSocket(serverSocket);
                        ioManager.write(ReturnCode.get(225));
                    }
                } catch (SocketTimeoutException e) {
                    ioManager.write(ReturnCode.get(425));
                } catch (IOException e) {
                    ioManager.write(String.format(ReturnCode.get(400), "400 can't open listen port"));
                } finally {
                    try {
                        if (serverSocket != null) serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        commands.put("PORT", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else if (!status.isLogged()) ioManager.write(ReturnCode.get(530));
            else {
                try {
                    String host = Tools.parseHost(parameters[1]);
                    int port = Tools.parsePort(parameters[1]);

                    ioManager.closeDataSocket();
                    Socket temp = new Socket(host, port);
                    ioManager.write(String.format(ReturnCode.get(200), "data socket set"));
                    ioManager.setDataSocket(temp);
                } catch (Tools.MisMatchError ignored) {
                    ioManager.write(ReturnCode.get(501));
                } catch (IOException ignored) {
                    ioManager.write(ReturnCode.get(425));
                }
            }
        });

        commands.put("LIST", (status, parameters, ioManager) -> {
            if (!status.isLogged()) ioManager.write(ReturnCode.get(530));
            else if (!ioManager.isDataSocketAvailable()) ioManager.write(ReturnCode.get(425));
            else {
                File directory;
                if (parameters.length == 1) directory = new File(Constants.BASE_DIR);
                else directory = new File(Constants.BASE_DIR + File.separator + parameters[1]);
                if (!directory.exists()) ioManager.write(ReturnCode.get(550));
                else {
                    ioManager.write(String.format(ReturnCode.get(200), "start listing files"));
                    try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(ioManager.getDataSocket().getOutputStream()))) {
                        for (File file : Objects.requireNonNull(directory.listFiles())) {
                            printWriter.print(file.getName() + " ");
                            printWriter.println(file.isDirectory() ? "1" : "0");
                            printWriter.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        commands.put("QUIT", (status, parameters, ioManager) -> {
            ioManager.write(ReturnCode.get(221));
            ioManager.close();
            status.setStop(true);
        });

        commands.put("TYPE", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else {
                if ("I".equalsIgnoreCase(parameters[1])) status.setType(Constants.IMAGE);
                else status.setType(Constants.ASCII);
                ioManager.write(String.format(ReturnCode.get(200), "set type successfully."));
            }
        });

        commands.put("MODE", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else {
                if ("B".equalsIgnoreCase(parameters[1])) status.setMode(Constants.BLOCK);
                else if ("C".equalsIgnoreCase(parameters[1])) status.setMode(Constants.COMPRESSED);
                else status.setMode(Constants.STREAM);
                ioManager.write(String.format(ReturnCode.get(200), "set mode successfully."));
            }
        });

        commands.put("STRU", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else {
                if ("R".equalsIgnoreCase(parameters[1])) status.setStructure(Constants.RECORD);
                else if ("P".equalsIgnoreCase(parameters[1])) status.setStructure(Constants.PAGE);
                else status.setStructure(Constants.FILE);
                ioManager.write(String.format(ReturnCode.get(200), "set structure successfully."));
            }
        });

        commands.put("ZIP", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else {
                status.setZipped("T".equalsIgnoreCase(parameters[1]));
                ioManager.write(String.format(ReturnCode.get(200), "set zipped successfully."));
            }
        });

        commands.put("RETR", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else if (!status.isLogged()) ioManager.write(ReturnCode.get(530));
            else if (!ioManager.isDataSocketAvailable()) ioManager.write(ReturnCode.get(425));
            else {
                String path = Constants.BASE_DIR + File.separator + parameters[1];
                //删去后缀.zip，获取要打包的文件名
                if (status.isZipped()) path = path.substring(0, path.length() - 4);
                File file = new File(path);
                if (!file.exists()) ioManager.write(ReturnCode.get(550));
                else {
                    ioManager.write(ReturnCode.get(150));

                    if (status.isZipped()) {
                        Tools.zip(path);
                        file = new File(path + ".zip");
                    }

                    Socket dataSocket = ioManager.getDataSocket();
                    Transmitter transmitter = (status.getType() == Constants.ASCII) ? new AsciiTransmitter(file, dataSocket) : new ImageTransmitter(file, dataSocket);
                    boolean flag = transmitter.retrieve(status.getStructure());
                    ioManager.write(ReturnCode.get(flag ? 250 : 450));

                    //若为File或Record结构，需要重新建立数据连接
                    if (status.getStructure() == Constants.FILE || status.getStructure() == Constants.RECORD) {
                        ioManager.closeDataSocket();
                    }

                    //若为zip模式，需要删除临时创建的zip压缩包
                    if (status.isZipped()) file.delete();
                }
            }
        });

        commands.put("STOR", (status, parameters, ioManager) -> {
            if (parameters.length < 2) ioManager.write(ReturnCode.get(501));
            else if (!status.isLogged()) ioManager.write(ReturnCode.get(530));
            else if (!ioManager.isDataSocketAvailable()) ioManager.write(ReturnCode.get(425));
            else {
                File file = new File(Constants.BASE_DIR + File.separator + parameters[1]);
                if (file.exists()) Tools.deleteFile(file.getPath());
                ioManager.write(ReturnCode.get(150));
                Socket dataSocket = ioManager.getDataSocket();
                Transmitter transmitter = (status.getType() == Constants.ASCII) ?
                        new AsciiTransmitter(file, dataSocket) : new ImageTransmitter(file, dataSocket);
                boolean flag = transmitter.store(status.getStructure());

                //若为File结构，需要重新建立数据连接
                if (status.getStructure() == Constants.FILE || status.getStructure() == Constants.RECORD) {
                    ioManager.closeDataSocket();
                }

                ioManager.write(ReturnCode.get(flag ? 250 : 450));

                if (status.isZipped()) {
                    Tools.unzip(file);
                    file.delete();
                }
            }
        });

        commands.put("NOOP", (status, parameters, ioManager) -> {
        });
    }

    public static void doCommand(String commandLine, Status status, IOManager ioManager) {
        String[] parameters = commandLine.split(" ");
        commands.getOrDefault(parameters[0].toUpperCase(), defaultDealer).deal(status, parameters, ioManager);
    }
}
