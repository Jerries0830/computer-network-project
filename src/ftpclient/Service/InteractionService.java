package com.example.ftpclient.Service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.ftpclient.Logic.Client.IOManager;
import com.example.ftpclient.Logic.Client.Status;
import com.example.ftpclient.Logic.Command.Command;
import com.example.ftpclient.Logic.Util.Constants;
import com.example.ftpclient.Logic.Util.Tools;
import com.example.ftpclient.R;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class InteractionService extends IntentService {
    private static final String ACTION_CONNECT = "com.example.ftpclient.action.connect";
    private static final String ACTION_COMMAND = "com.example.ftpclient.action.command";
    private static final String ACTION_UPLOAD = "com.example.ftpclient.action.upload";
    private static final String ACTION_DOWNLOAD = "com.example.ftpclient.action.download";
    private static final String ACTION_LIST = "com.example.ftpclient.action.list";
    private static final String ACTION_QUIT = "com.example.ftpclient.action.quit";

    private static final String HOST = "com.example.ftpclient.extra.host";
    private static final String PORT = "com.example.ftpclient.extra.port";
    private static final String USER = "com.example.ftpclient.extra.user";
    private static final String PASSWORD = "com.example.ftpclient.extra.password";
    private static final String COMMAND = "com.example.ftpclient.extra.command";
    private static final String NAME = "com.example.ftpclient.extra.name";
    private static final String PATH = "com.example.ftpclient.extra.path";
    private static final String ISFILE = "com.example.ftpclient.extra.isFile";

    public static final String CONNECT_STATUS = "connect_status";
    public static final String RESPONSE = "response";
    public static final String FLAG = "flag";
    public static final String TIME = "time";
    public static final String DIR = "dir";
    public static final String FILES = "files";

    private static Status status;
    private static IOManager ioManager;

    public InteractionService() {
        super("InteractionService");
    }

    public static void doConnect(Context context, String host, String port, String user, String password) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_CONNECT);
        intent.putExtra(HOST, host);
        intent.putExtra(PORT, port);
        intent.putExtra(USER, user);
        intent.putExtra(PASSWORD, password);
        context.startService(intent);
    }

    public static void doCommand(Context context, String command) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_COMMAND);
        intent.putExtra(COMMAND, command);
        context.startService(intent);
    }

    public static void doUpload(Context context, String fileName, String filePath) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(NAME, fileName);
        intent.putExtra(PATH, filePath);
        context.startService(intent);
    }

    public static void doDownload(Context context, String filePath, boolean isFile) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(PATH, filePath);
        intent.putExtra(ISFILE, isFile);
        context.startService(intent);
    }

    public static void doList(Context context, String filePath) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_LIST);
        intent.putExtra(PATH, filePath);
        context.startService(intent);
    }

    public static void doQuit(Context context) {
        Intent intent = new Intent(context, InteractionService.class);
        intent.setAction(ACTION_QUIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CONNECT.equals(action)) {
                final String host = intent.getStringExtra(HOST);
                final String port = intent.getStringExtra(PORT);
                final String user = intent.getStringExtra(USER);
                final String password = intent.getStringExtra(PASSWORD);
                handleConnect(host, port, user, password);
            } else if (ACTION_COMMAND.equals(action)) {
                final String command = intent.getStringExtra(COMMAND);
                handleCommand(command);
            } else if (ACTION_UPLOAD.equals(action)) {
                final String fileName = intent.getStringExtra(NAME);
                final String filePath = intent.getStringExtra(PATH);
                handleUpload(fileName, filePath);
            } else if (ACTION_DOWNLOAD.equals(action)) {
                final String filePath = intent.getStringExtra(PATH);
                final boolean isFile = intent.getBooleanExtra(ISFILE, false);
                handleDownload(filePath, isFile);
            } else if (ACTION_LIST.equals(action)) {
                final String dirPath = intent.getStringExtra(PATH);
                handleList(dirPath);
            } else if (ACTION_QUIT.equals(action)) {
                handleQuit();
            }
        }
    }

    private void handleConnect(String host, String port, String user, String password) {
        //若之前处于连接状态，需要断开连接并重新建立连接
        handleQuit();

        int connectStatus;
        try {
            status = new Status();
            ioManager = new IOManager();

            Socket commandSocket = new Socket(host, Integer.parseInt(port));
            commandSocket.setSoTimeout(Constants.CONNECT_TIME_OUT);
            ioManager.setCommandSocket(commandSocket);

            if (doCommandWithReturnCode("USER " + user) == 230 || doCommandWithReturnCode("PASS " + password) == 230) {
                handleCommand("PASV");
                connectStatus = 1;
            } else connectStatus = 0;
        } catch (IOException | NumberFormatException ignored) {
            connectStatus = -1;
        }

        Intent intent = new Intent(getString(R.string.action_connect));
        intent.putExtra(CONNECT_STATUS, connectStatus);
        getApplicationContext().sendBroadcast(intent);
    }

    private void handleCommand(String command) {
        String response = doCommandWithResponse(command);
        Intent intent = new Intent(getString(R.string.action_command));
        intent.putExtra(RESPONSE, response);
        getApplicationContext().sendBroadcast(intent);
    }

    private int doCommandWithReturnCode(String command) {
        String response = doCommandWithResponse(command);
        return Tools.getReturnCode(response);
    }

    private String doCommandWithResponse(String command) {
        Command.doCommand(command, status, ioManager);
        return ioManager.getResponse();
    }

    private void handleUpload(String fileName, String filePath) {
        boolean flag = true;
        long start = System.currentTimeMillis();

        if (!status.isZipped()) {
            File file = new File(filePath);
            if (file.isFile()) flag = uploadSingle(fileName, filePath);
            else if (file.isDirectory()) {
                ArrayList<String> paths = new ArrayList<>();
                Tools.getFilesInDir(file, paths);
                for (String path : paths)
                    flag = flag && uploadSingle(path.substring(path.lastIndexOf(File.separator) + 1), path);
            }
        } else {
            Tools.zip(filePath);
            flag = uploadSingle(fileName + ".zip", filePath + ".zip");

            //若为zip模式，需要删除临时创建的zip压缩包
            File file = new File(filePath + ".zip");
            file.delete();
        }

        long end = System.currentTimeMillis();

        Intent intent = new Intent(getString(R.string.action_upload));
        intent.putExtra(FLAG, flag);
        intent.putExtra(TIME, end - start);
        getApplicationContext().sendBroadcast(intent);
    }

    private boolean uploadSingle(String fileName, String filePath) {
        String command = String.format("STOR %s %s", fileName, filePath);
        Command.doCommand(command, status, ioManager);
        return Tools.getReturnCode(ioManager.getResponse()) == 250;
    }

    private void handleDownload(String filePath, boolean isFile) {
        boolean flag;
        long start = System.currentTimeMillis();

        if (!status.isZipped()) flag = downloadSingle(filePath, isFile);
        else {
            Tools.deleteFile(Constants.BASE_DIR + File.separator + filePath);
            flag = downloadSingle(filePath + ".zip", true);
            File file = new File(Constants.BASE_DIR + File.separator + filePath + ".zip");
            Tools.unzip(file);
            file.delete();
        }

        long end = System.currentTimeMillis();

        Intent intent = new Intent(getString(R.string.action_download));
        intent.putExtra(FLAG, flag);
        intent.putExtra(TIME, end - start);
        getApplicationContext().sendBroadcast(intent);
    }

    private boolean downloadSingle(String filePath, boolean isFile) {
        if (isFile) {
            String command = String.format("RETR %s", filePath);
            Command.doCommand(command, status, ioManager);
            return Tools.getReturnCode(ioManager.getResponse()) == 250;
        } else {
            String command = String.format("LIST %s", filePath);
            String files = Command.doCommand(command, status, ioManager).trim();

            boolean flag = true;
            if (!files.equals("")) {
                String[] temp = files.split(" ");
                for (int i = 0; i < temp.length; i += 2) {
                    String path = filePath + File.separator + temp[i];
                    boolean subIsFile = temp[i + 1].equals("0");
                    flag = flag && downloadSingle(path, subIsFile);
                }
            }
            return flag;
        }
    }

    private void handleList(String dirPath) {
        String command = String.format("LIST %s", dirPath);
        String files = Command.doCommand(command, status, ioManager).trim();
        Intent intent = new Intent(getString(R.string.action_list));
        intent.putExtra(DIR, dirPath);
        intent.putExtra(FILES, files);
        getApplicationContext().sendBroadcast(intent);
    }

    private void handleQuit() {
        if (ioManager != null && ioManager.isConnected()) {
            String command = "QUIT";
            Command.doCommand(command, status, ioManager);
        }
    }

    public static int getType() {
        return status.getType();
    }

    public static int getMode() {
        return status.getMode();
    }

    public static int getStructure() {
        return status.getStructure();
    }

    public static boolean getZipped() {
        return status.isZipped();
    }
}