package com.example.ftpserver.Logic.Server;

public class Status {
    private String username;
    private boolean logged = false;
    private boolean stop = false;

    private int type = 0;                       //0 = Ascii, 1 = Binary
    private int mode = 0;                       //0 = Stream, 1 = Block, 2 = Compressed
    private int structure = 0;                  //0 = File, 1 = Record, 2 = Page
    private boolean zipped = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getStructure() {
        return structure;
    }

    public void setStructure(int structure) {
        this.structure = structure;
    }

    public boolean isZipped() {
        return zipped;
    }

    public void setZipped(boolean zipped) {
        this.zipped = zipped;
    }
}
