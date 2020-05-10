package com.pojul.objectsocket.utils;

import java.io.File;

public class Constant {
	public final static String HOST = "192.168.86.122";
	/*public final static String HOST = "47.93.31.206";*/

	//public final static String BASE_LOCAL_PATH = "D:\\websource\\";
	public final static String BASE_LOCAL_PATH = "/root/websource/";

	public static final String BASE_URL = "http://" + HOST + ":8080/resources/";
	public static final String SERVICE_LOCAL_FILE_PATH = BASE_LOCAL_PATH;
	public static final String USER_PHOTO_PATH = BASE_LOCAL_PATH + "photo" + File.separator;
	/*0: local; 1: server*/
	public static int STORAGE_TYPE = 1;
	public final static int PORT = 57142;
}
