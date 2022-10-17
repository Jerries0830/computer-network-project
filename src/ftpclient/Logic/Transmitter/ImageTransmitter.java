package com.example.ftpclient.Logic.Transmitter;

import com.example.ftpclient.Logic.Util.Constants;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ImageTransmitter extends Transmitter {
    public ImageTransmitter(File file, Socket socket) {
        super(file, socket);
    }

    @Override
    public void retrieve(int structure) {
        try {
            InputStream inputStream = socket.getInputStream();

            if (structure == Constants.FILE) {
                OutputStream outputStream = new FileOutputStream(file);
                dealFile(inputStream, outputStream);
                outputStream.close();
            } else if (structure == Constants.PAGE) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "rw");
                byte[] buf = new byte[Constants.HEAD_LENGTH + Constants.DATA_MAX_LENGTH];

                //标识开始
                inputStream.read(buf, 0, Constants.HEAD_LENGTH);

                //中间传输
                while (true) {
                    inputStream.read(buf, 0, Constants.HEAD_LENGTH + Constants.DATA_MAX_LENGTH);

                    //标识结束
                    if (buf[Constants.HEAD_LENGTH - 1] == Byte.parseByte(Constants.END + "")) break;

                    try {
                        targetFile.seek(getPageNumber(buf) * Constants.DATA_MAX_LENGTH);
                        targetFile.write(Arrays.copyOfRange(buf, Constants.HEAD_LENGTH, Constants.HEAD_LENGTH + getDataLength(buf)));
                    } catch (IOException ignored) {
                    }
                }

                targetFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(int structure) {
        try (InputStream inputStream = new FileInputStream(file)) {
            OutputStream outputStream = socket.getOutputStream();

            if (structure == Constants.FILE) dealFile(inputStream, outputStream);
            else if (structure == Constants.PAGE) {
                //标识开始
                outputStream.write(getHeader(0, 0, Constants.BEGIN), 0, Constants.HEAD_LENGTH);
                outputStream.flush();

                //中间传输
                int pageNumber = 0;
                byte[] buf = new byte[Constants.DATA_MAX_LENGTH];
                int length = inputStream.read(buf, 0, buf.length);
                while (length != -1) {
                    byte[] page = combine(getHeader(pageNumber++, length, Constants.MIDDLE), buf);
                    outputStream.write(page);
                    outputStream.flush();
                    length = inputStream.read(buf, 0, buf.length);
                }

                //标识结束
                outputStream.write(getHeader(0, 0, Constants.END), 0, Constants.HEAD_LENGTH);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dealFile(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] buf = new byte[1024];
            int length = inputStream.read(buf, 0, buf.length);
            while (length != -1) {
                outputStream.write(buf, 0, length);
                outputStream.flush();
                length = inputStream.read(buf, 0, buf.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getPageNumber(byte[] buf) {
        long buf1 = buf[1] & 0xff;
        long buf2 = buf[2] & 0xff;
        long buf3 = buf[3] & 0xff;
        long buf4 = buf[4] & 0xff;
        return buf1 * 256 * 256 * 256 + buf2 * 256 * 256 + buf3 * 256 + buf4;
    }

    private static int getDataLength(byte[] buf) {
        int buf5 = buf[5] & 0xff;
        int buf6 = buf[6] & 0xff;
        return buf5 * 256 + buf6;
    }

    //头部长度默认为8，第0位表示头部长度，第1至4位表示页号，第5和6位表示数据长度，第7位表示页类型
    private static byte[] getHeader(int pageNumber, int dataLength, int type) {
        byte[] head = new byte[Constants.HEAD_LENGTH];
        head[0] = Constants.HEAD_LENGTH;
        head[1] = (byte) ((pageNumber >> 24) & 0xff);
        head[2] = (byte) ((pageNumber >> 16) & 0xff);
        head[3] = (byte) ((pageNumber >> 8) & 0xff);
        head[4] = (byte) (pageNumber & 0xff);
        head[5] = (byte) ((dataLength >> 8) & 0xff);
        head[6] = (byte) (dataLength & 0xff);
        head[7] = (byte) (type);
        return head;
    }

    private static byte[] combine(byte[] btX, byte[] btY) {
        byte[] btZ = new byte[btX.length + btY.length];
        System.arraycopy(btX, 0, btZ, 0, btX.length);
        System.arraycopy(btY, 0, btZ, btX.length, btY.length);
        return btZ;
    }
}
