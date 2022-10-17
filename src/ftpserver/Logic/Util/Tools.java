package com.example.ftpserver.Logic.Util;

import com.example.ftpserver.MyApplication;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;

public class Tools {
    //socket related
    public static int getFreePort() {
        return getFreePort(Constants.MIN_PORT);
    }

    public static int getFreePort(int minPort) {
        for (int port = minPort; port < Constants.MAX_PORT; port++) {
            if (!isPortUsed(port)) return port;
        }
        return -1;
    }

    public static boolean isPortUsed(int port) {
        try {
            Socket socket = new Socket(Constants.LOCAL_HOST, port);
            socket.close();
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    public static String getIP() {
        String hostIp = null;
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return hostIp;
    }

    public static String[] getIPArray() {
        String IP = getIP();
        return IP.split("\\.");
    }

    public static String parseHost(String raw) throws MisMatchError {
        String[] parts = parseHostAndPort(raw);
        return String.format("%s.%s.%s.%s", parts[0], parts[1], parts[2], parts[3]);
    }

    public static int parsePort(String raw) throws MisMatchError {
        String[] parts = parseHostAndPort(raw);
        int count = Integer.parseInt(parts[4]);
        int offset = Integer.parseInt(parts[5]);
        return count * 256 + offset;
    }

    public static boolean verify(String user, String password) {
        HashMap<String, String> users = MyApplication.getUsers();
        return users.containsKey(user) && password.equals(users.get(user));
    }

    private static String[] parseHostAndPort(String target) throws MisMatchError {
        int beginIndex = target.indexOf('(');
        int endIndex = target.lastIndexOf(')');
        String[] parts = target.substring(beginIndex + 1, endIndex == -1 ? target.length() : endIndex).split(",");
        if (parts.length != 6) throw new MisMatchError();
        return parts;
    }

    //file related
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) file.delete();
            else if (file.isDirectory()) {
                for (File subFile : Objects.requireNonNull(file.listFiles())) {
                    subFile.delete();
                }
            }
        }
    }

    public static long getSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public static String getMD5(String path) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(path));
        } catch (IOException ignored) {
            return "";
        }
    }

    //zip related
    public static File zip(String src) {
        return zip(src, src + ".zip");
    }

    public static File zip(String src, String dest) {
        File srcF = new File(src);
        if (!srcF.exists()) {
            return null;
        } else {
            File destF = new File(dest);

            if (destF.exists() && destF.isFile()) {
                destF.delete();
            }

            try {
                ZipFile zipFile = new ZipFile(dest);
                ZipParameters parameters = new ZipParameters();
                parameters.setCompressionMethod(8);
                parameters.setCompressionLevel(5);
                if (srcF.isDirectory()) {
                    zipFile.addFolder(srcF, parameters);
                } else {
                    zipFile.addFile(srcF, parameters);
                }

                return destF;
            } catch (Exception var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    public static boolean unzip(File file) {
        return unzip(file, file.getParent());
    }

    public static boolean unzip(File file, String dir) {
        if (file == null) {
            return false;
        } else {
            try {
                ZipFile zipFile = new ZipFile(file);

                File dirF = new File(dir);
                if (!dirF.exists() || !dirF.isDirectory()) {
                    dirF.mkdirs();
                }

                zipFile.extractAll(dir);
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static class MisMatchError extends Error {
    }
}

