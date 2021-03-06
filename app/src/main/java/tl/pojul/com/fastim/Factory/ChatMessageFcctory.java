package tl.pojul.com.fastim.Factory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pojul.fastIM.message.chat.AudioCommuMessage;
import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicCommuMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicCommuMessage;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.chat.TextCommuMessage;
import com.pojul.fastIM.message.chat.VideoCommuMessage;
import com.pojul.fastIM.message.chat.VideoMessage;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.io.File;

import tl.pojul.com.fastim.util.Constant;
import tl.pojul.com.fastim.util.FileUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class ChatMessageFcctory {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_TEXT_PIC= 1;
    public static final int TYPE_PIC = 2;
    public static final int TYPE_NET_PIC = 3;
    public static final int TYPE_AUDIO = 4;
    public static final int TYPE_VIDEO= 5;
    public static final int TYPE_FILE = 6;
    private final static String TAG = "ChatMessageFcctory";

    public ChatMessage create(int messageType, @NonNull String filePath, int chatRoomType){
        ChatMessage chatMessage;
        CommunityMessage communityMessage = null;
        switch (messageType){
            case TYPE_PIC:
                chatMessage =  createPicMessage(filePath, chatRoomType);
                communityMessage = new PicCommuMessage();
                break;
            case TYPE_AUDIO:
                chatMessage = createAudioMessage(filePath, chatRoomType);
                communityMessage = new AudioCommuMessage();
                break;
            case TYPE_VIDEO:
                chatMessage =  createVideoMessage(filePath, chatRoomType);
                communityMessage = new VideoCommuMessage();
                break;
            case TYPE_FILE:
                if(FileClassUtil.isPathPicFile(filePath)){
                    chatMessage =  createPicMessage(filePath, chatRoomType);
                    communityMessage = new PicCommuMessage();
                } else if(FileClassUtil.isPathAudioFile(filePath)){
                    chatMessage =  createAudioMessage(filePath, chatRoomType);
                    communityMessage = new AudioCommuMessage();
                } else if(FileClassUtil.isPathVideoFile(filePath)){
                    chatMessage =  createVideoMessage(filePath, chatRoomType);
                    communityMessage = new VideoCommuMessage();
                } else {
                    chatMessage = createFileMessage(filePath, chatRoomType);
                }
                break;
            default:
                chatMessage =  new ChatMessage();
                break;
        }
        if(chatRoomType == 3 && communityMessage != null){
            communityMessage.setContent(chatMessage);
            communityMessage.setChatType(chatRoomType);
            return communityMessage;
        }else{
            return chatMessage;
        }
    }

    public PicMessage createPicMessage(@NonNull String filePath, int chatRoomType){
        PicMessage picMessage = new PicMessage();
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            picMessage.setWidth(options.outWidth);
            picMessage.setHeight(options.outHeight);
        }catch(Exception e){}
        picMessage.setChatType(chatRoomType);
        picMessage.setPic(FileClassUtil.createStringFile(filePath));
        return picMessage;
    }

    public AudioMessage createAudioMessage(@NonNull String filePath, int chatRoomType){
        AudioMessage audioMessage = new AudioMessage();
        audioMessage.setChatType(chatRoomType);
        audioMessage.setAudio(FileClassUtil.createStringFile(filePath));
        return audioMessage;
    }

    public FileMessage createFileMessage(@NonNull String filePath, int chatRoomType){
        FileMessage fileMessage = new FileMessage();
        fileMessage.setChatType(chatRoomType);
        fileMessage.setFile(FileClassUtil.createStringFile(filePath));
        return fileMessage;
    }

    public VideoMessage createVideoMessage(@NonNull String filePath, int chatRoomType){
        VideoMessage videoMessage = new VideoMessage();
        videoMessage.setChatType(chatRoomType);
        videoMessage.setVideo(FileClassUtil.createStringFile(filePath));
        try{
            MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象mmr
            File file=new File(filePath);//实例化File对象file，指定文件路径为/storage/sdcard/Music/music1.mp3
            mmr.setDataSource(file.getAbsolutePath());
            Bitmap bitmap = mmr.getFrameAtTime();
            videoMessage.setVideoWidth(bitmap.getWidth());
            videoMessage.setVideoHeight(bitmap.getHeight());
            String firstPicName = FileClassUtil.getFileName(filePath, StorageType.LOCAL);
            int index = firstPicName.lastIndexOf(".");
            firstPicName = firstPicName.substring(0, index) + ".jpg";
            String firstPicPath = (SPUtil.getInstance().getString(Constant.BASE_STORAGE_PATH ) + "/footstep/pic/" + firstPicName);
            FileUtil.saveBitmap(bitmap, firstPicPath);
            videoMessage.setFirstPic(FileClassUtil.createStringFile(firstPicPath));
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        return videoMessage;
    }

    public TextChatMessage createTextMessage(@NonNull String text, int chatRoomType){
        TextChatMessage textChatMessage = new TextChatMessage();
        textChatMessage.setChatType(chatRoomType);
        textChatMessage.setText(text);
        return textChatMessage;
    }

    public CommunityMessage createCommunityMessage(ChatMessage baseTypeMessage){
        CommunityMessage communityMessage = null;
        if(baseTypeMessage instanceof TextChatMessage){
            communityMessage = new TextCommuMessage();
        }else if(baseTypeMessage instanceof NetPicMessage){
            communityMessage = new NetPicCommuMessage();
        }
        if(communityMessage == null){
            communityMessage = new CommunityMessage();
        }
        baseTypeMessage.setChatType(3);
        communityMessage.setContent(baseTypeMessage);
        communityMessage.setChatType(3);
        return communityMessage;
    }

}
