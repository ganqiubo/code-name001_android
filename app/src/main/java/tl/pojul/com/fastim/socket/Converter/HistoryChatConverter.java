package tl.pojul.com.fastim.socket.Converter;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Message;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.DateMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.util.DateUtil;

public class HistoryChatConverter {

    private static final String TAG = "HistoryChatConverter";

    public List<ChatMessage> converter(ArrayList<Message> messages){
        List<ChatMessage> rawChatMessages = new ArrayList<>();
        Gson gson = new Gson();
        for(int i =0; i < messages.size(); i++){
            Message message = messages.get(i);
            try {
                ChatMessage chatMessage = (ChatMessage) gson
                        .fromJson(message.getMessageContent(), Class.forName(message.getMessageClass()));
                chatMessage.setIsSend(1);
                rawChatMessages.add(chatMessage);
            } catch (Exception e) {
                LogUtil.i(TAG,"解析历史数据失败");
            }
        }
        List<ChatMessage> chatMessages = insertDate(rawChatMessages);
        return chatMessages;
    }

    /**
     * 添加时间标志
     */
    public static List<ChatMessage> insertDate(List<ChatMessage> rawChatMessages){
        List<ChatMessage> chatMessages = new ArrayList<>();
        for(int i = 0; i< rawChatMessages.size(); i++){
            if(i == 0){
                chatMessages.add(new DateMessage(DateUtil.transformToRoughDate(rawChatMessages.get(0).getSendTime())));
            }
            if(i != (rawChatMessages.size() -1)){
                chatMessages.add(rawChatMessages.get(i));

                if(DateUtil.isDiffDay(rawChatMessages.get(i).getSendTime(), rawChatMessages.get((i +1)).getSendTime())){
                    chatMessages.add(new DateMessage(DateUtil.transformToRoughDate(rawChatMessages.get((i +1)).getSendTime())));
                }
            }
            if(i == (rawChatMessages.size() - 1) && i != 0){
                chatMessages.add(rawChatMessages.get((rawChatMessages.size() - 1)));
            }
        }
        if(rawChatMessages.size() == 1){
            chatMessages.add(rawChatMessages.get(0));
        }
        return chatMessages;
    }

}
