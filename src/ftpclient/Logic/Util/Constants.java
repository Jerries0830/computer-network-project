package com.example.ftpclient.Logic.Util;

import com.example.ftpclient.MyApplication;

public class Constants {
    //storage
    public static final String BASE_DIR = MyApplication.getContext().getFilesDir().getPath();
    //socket related
    public static String LOCAL_HOST = "127.0.0.1";
    public static final int CONNECT_TIME_OUT = 10000;
    public static final int LISTEN_PORT = 1026;
    public static int MIN_PORT = 1025;
    public static int MAX_PORT = 65535;
    //file type
    public static final int ASCII = 0;
    public static final int IMAGE = 1;
    //file mode
    public static final int STREAM = 0;
    public static final int BLOCK = 1;
    public static final int COMPRESSED = 2;
    //file structure
    public static final int FILE = 0;
    public static final int RECORD = 1;
    public static final int PAGE = 2;
    //page type
    public static final int BEGIN = 2;
    public static final int MIDDLE = 1;
    public static final int END = 0;
    //page header
    public static final int HEAD_LENGTH = 8;
    public static final int DATA_MAX_LENGTH = 16376;
    //debug
    public static final String MYTAG = "myTag";
}
