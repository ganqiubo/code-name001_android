package tl.pojul.com.fastim.http.converter;

import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduPicConverter extends NetPicConverter{

    @Override
    public ArrayList<NetPicMessage> converter(String responseStr) {
        ArrayList<NetPicMessage> netPics= new ArrayList<>();
        Pattern pattern = Pattern.compile("\"thumbURL\":\".*?\"");
        Matcher matcher = pattern.matcher(responseStr);
        while(matcher.find()){
            String thumbURL = responseStr.substring((matcher.start() + 12), (matcher.end() - 1));
            NetPicMessage netPicMessage = new NetPicMessage("baidu");
            StringFile sf = new StringFile(StorageType.SERVER);
            sf.setFilePath(thumbURL);
            sf.setFileType(FileClassUtil.getFileType(thumbURL));
            netPicMessage.setThumbURL(sf);
            netPicMessage.setPicType(sf.getFileType());
            if(FileClassUtil.isPicFile(sf.getFileType())){
                netPics.add(netPicMessage);
            }
        }

        pattern = Pattern.compile("\"objURL\":\".*?\"");
        matcher = pattern.matcher(responseStr);
        int index = 0;
        while(matcher.find()){
            String fullURL = responseStr.substring((matcher.start() + 10), (matcher.end() - 1));
            if(index < netPics.size()){
                NetPicMessage netPicMessage = netPics.get(index);
                if(netPicMessage == null){
                    continue;
                }
                StringFile sf = new StringFile(StorageType.SERVER);
                sf.setFilePath(fullURL);
                sf.setFileType(FileClassUtil.getFileType(fullURL));
                if(FileClassUtil.isPicFile(sf.getFileType())){
                    netPicMessage.setFullURL(sf);
                }
            }
            index ++;
        }

        //可能会多处几个缩略图
        int dropNum = netPics.size() - index;
        for(int i = 0; i < dropNum ; i++){
            if(netPics.size() <= 0){
                break;
            }
            netPics.remove((netPics.size() - 1));
        }

        pattern = Pattern.compile("\"width\":.*?,");
        matcher = pattern.matcher(responseStr);
        index = 0;
        while(matcher.find()){
            int width = 0;
            try{
                width = Integer.parseInt(responseStr.substring((matcher.start() + 8), (matcher.end() - 1)));
            }catch (Exception e){}
            if(index < netPics.size()){
                NetPicMessage netPicMessage = netPics.get(index);
                if(netPicMessage == null){
                    continue;
                }
                netPicMessage.setWidth(width);
            }
            index ++;
        }

        pattern = Pattern.compile("\"height\":.*?,");
        matcher = pattern.matcher(responseStr);
        index = 0;
        while(matcher.find()){
            int height = 0;
            try{
                height = Integer.parseInt(responseStr.substring((matcher.start() + 9), (matcher.end() - 1)));
            }catch (Exception e){}
            if(index < netPics.size()){
                NetPicMessage netPicMessage = netPics.get(index);
                if(netPicMessage == null){
                    continue;
                }
                netPicMessage.setHeight(height);
            }
            index ++;
        }
        return netPics;
    }

}
