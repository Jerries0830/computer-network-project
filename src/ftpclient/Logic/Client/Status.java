package com.example.ftpclient.Logic.Client;

public class Status {
    private int type = 0;                       //0 = Ascii, 1 = Image
    private int mode = 0;                       //0 = Stream, 1 = Block, 2 = Compressed
    private int structure = 0;                  //0 = File, 1 = Record, 2 = Page
    private boolean isPassive = true;
    private boolean zipped = false;

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

    public boolean isPassive() {
        return isPassive;
    }

    public void setPassive(boolean passive) {
        isPassive = passive;
    }

    public boolean isZipped() {
        return zipped;
    }

    public void setZipped(boolean zipped) {
        this.zipped = zipped;
    }
}
