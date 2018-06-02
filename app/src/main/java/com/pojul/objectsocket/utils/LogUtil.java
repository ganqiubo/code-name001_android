package com.pojul.objectsocket.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

	public static boolean allowD = false;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;
    
    public static void d(String TAG, String content) {
        if (!allowD) {
            return;
        }
        String message = TAG + "  D: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
    
    public static void d(String content) {
        if (!allowD) {
            return;
        }
        String message = "  D: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
	
    public static void e(String TAG, String content) {
        if (!allowE) {
            return;
        }
        String message = TAG + "  E: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
    
    public static void e(String content) {
        if (!allowE) {
            return;
        }
        String message = "  E: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
    
    public static void i(String TAG, String content) {
        if (!allowI) {
            return;
        }
        String message = TAG + "  I: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
    
    public static void i(String content) {
        if (!allowI) {
            return;
        }
        String message = "  I: " + 
        		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()) + "  " +
        		content;
        System.out.println(message);
    }
    
    
}
