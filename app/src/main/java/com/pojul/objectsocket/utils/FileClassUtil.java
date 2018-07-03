package com.pojul.objectsocket.utils;

import com.pojul.objectsocket.constant.FileType;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.message.StringFile;

import java.io.File;

public class FileClassUtil {

    /**
     *@param path
     * */
    public static String getFileType(String path){
        int index = path.lastIndexOf(".");
        if(index != -1){
            return path.substring((index + 1), path.length());
        }
        return "";
    }

    public static String getFileName(String path, int storageType){
        if(storageType == StorageType.SERVER){
            return "";
        }else{
            int index = path.lastIndexOf("/");
            if(index == -1){
                index = 0;
            }
            return path.substring((index + 1) , path.length());
        }
    }

    public static String getNetFileName(String path){
        int index = path.lastIndexOf("/");
        if(index == -1){
            index = 0;
        }
        return path.substring((index + 1) , path.length());
    }

    /**
     *@param path
     * */
    public static boolean isPathPicFile(String path){
        return isPicFile(getFileType(path));
    }

    /**
     *@param fileType
     * */
    public static boolean isPicFile(String fileType){
        if(fileType.length() > 4){
            return false;
        }
        String uppercase = fileType.toUpperCase();
        switch (uppercase){
            case FileType.PIC_TYPE_AI:
            case FileType.PIC_TYPE_BMP:
            case FileType.PIC_TYPE_CDR:
            case FileType.PIC_TYPE_DXF:
            case FileType.PIC_TYPE_EMF:
            case FileType.PIC_TYPE_EPS:
            case FileType.PIC_TYPE_EXIF:
            case FileType.PIC_TYPE_FLIC:
            case FileType.PIC_TYPE_FPX:
            case FileType.PIC_TYPE_GIF:
            case FileType.PIC_TYPE_HDRI:
            case FileType.PIC_TYPE_ICO:
            case FileType.PIC_TYPE_JPEG:
            case FileType.PIC_TYPE_JPG:
            case FileType.PIC_TYPE_PCD:
            case FileType.PIC_TYPE_PCX:
            case FileType.PIC_TYPE_PNG:
            case FileType.PIC_TYPE_PSD:
            case FileType.PIC_TYPE_RAW:
            case FileType.PIC_TYPE_SVG:
            case FileType.PIC_TYPE_TGA:
            case FileType.PIC_TYPE_TIFF:
            case FileType.PIC_TYPE_UFO:
            case FileType.PIC_TYPE_WEBP:
            case FileType.PIC_TYPE_WMF:
                return true;
        }
        return false;
    }

    /**
     *@param path
     * */
    public static boolean isPathAudioFile(String path){
        return isAudioFile(getFileType(path));
    }

    /**
     *@param fileType
     * */
    public static boolean isAudioFile(String fileType){
        String uppercase = fileType.toUpperCase();
        switch (uppercase){
            case FileType.AUDIO_TYPE_WAV :
            case FileType.AUDIO_TYPE_MP3 :
            case FileType.AUDIO_TYPE_RA :
            case FileType.AUDIO_TYPE_RMA :
            case FileType.AUDIO_TYPE_WMA :
            case FileType.AUDIO_TYPE_MID :
            case FileType.AUDIO_TYPE_MIDI :
            case FileType.AUDIO_TYPE_CD :
            case FileType.AUDIO_TYPE_RMI :
            case FileType.AUDIO_TYPE_XMI :
            case FileType.AUDIO_TYPE_OGG :
            case FileType.AUDIO_TYPE_VQF :
            case FileType.AUDIO_TYPE_TVQ :
            case FileType.AUDIO_TYPE_MOD :
            case FileType.AUDIO_TYPE_APE :
            case FileType.AUDIO_TYPE_AAC :
                return true;
        }
        return false;
    }

    /**
     *@param path
     * */
    public static boolean isPathVideoFile(String path){
        return isVideoFile(getFileType(path));
    }

    /**
     *@param fileType
     * */
    public static boolean isVideoFile(String fileType){
        String uppercase = fileType.toUpperCase();
        switch (uppercase){
            case FileType.VIDEO_TYPE_MP4 :
            case FileType.VIDEO_TYPE_AVI :
            case FileType.VIDEO_TYPE_MOV :
            case FileType.VIDEO_TYPE_WMV :
            case FileType.VIDEO_TYPE_ASF :
            case FileType.VIDEO_TYPE_NAVI :
            case FileType.VIDEO_TYPE_3GP :
            case FileType.VIDEO_TYPE_MKV :
            case FileType.VIDEO_TYPE_F4V :
            case FileType.VIDEO_TYPE_FLV :
            case FileType.VIDEO_TYPE_RMVB :
            case FileType.VIDEO_TYPE_WEBM :
                return true;
        }
        return false;
    }

    /**
     *@param url
     * */
    public static boolean isHttpUrl(String url){
        if(url.length() < 7){
            return false;
        }
        if("http://".equals(url.substring(0, 7)) || "https://".equals(url.substring(0, 8))){
            return true;
        }
        return false;
    }

    /**
     * @param path
     * */
    public static StringFile createStringFile(String path){
        if(path == null){
            return null;
        }
        StringFile sf;
        if(isHttpUrl(path)){
            sf = new StringFile(StorageType.SERVER);
        }else{
            sf = new StringFile(StorageType.LOCAL);
            File file = new File(path);
            if(file.exists()){
                sf.setFileSize(file.length());
            }
        }
        sf.setFileType(getFileType(path));
        sf.setFilePath(path);
        sf.setFileName(getFileName(path, sf.getStorageType()));
        return sf;
    }

}
