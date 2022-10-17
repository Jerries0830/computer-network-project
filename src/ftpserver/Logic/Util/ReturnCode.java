package com.example.ftpserver.Logic.Util;

import java.util.HashMap;

public class ReturnCode {
    static final HashMap<Integer, String> codeMapping = new HashMap<>();

    static{
        codeMapping.put(125, "125 Data connection already open; transfer starting.");
        codeMapping.put(150, "150 File status okay; about to open data connection.");
        codeMapping.put(200, "200 %s");
        codeMapping.put(202, "202 Command not implemented, superfluous at this site.");
        codeMapping.put(221, "221 Service closing control connection. Logged out if appropriate");
        codeMapping.put(225, "225 Data connection open; no transfer in progress.");
        codeMapping.put(226, "226 Closing data connection. Requested file action successful (for example, file transfer or file abort).");
        codeMapping.put(227, "227 Entering Passive Mode(%s,%s,%s,%s,%d,%d)");
        codeMapping.put(230, "230 User logged in, proceed.");
        codeMapping.put(250, "250 Requested file action okay, completed.");
        codeMapping.put(331, "331 User name okay, need password.");
        codeMapping.put(332, "332 Need account for login.");
        codeMapping.put(400, "400 %s");
        codeMapping.put(425, "425 Can’t open data connection.");
        codeMapping.put(426, "426 Connection closed; transfer aborted.");
        codeMapping.put(450, "450 Requested file action not taken. File unavailable (e.g., file busy).");
        codeMapping.put(500, "500 Syntax error, command unrecognized. This may include errors such as command line too long.");
        codeMapping.put(501, "501 Syntax error in parameters or arguments.");
        codeMapping.put(530, "530 Not logged in.");
        codeMapping.put(550, "550 Requested action not taken. File unavailable (e.g., file not found, no access).");
    }

    public static String get(int returnCode){
        return codeMapping.get(returnCode);
    }
}
