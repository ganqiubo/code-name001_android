package tl.pojul.com.fastim.socket.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.Message;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.entity.UserFilter;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.DateMessage;
import com.pojul.fastIM.message.chat.TagCommuMessage;
import com.pojul.objectsocket.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.util.ArrayUtil;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.SPUtil;

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

    public List<ChatMessage> converter(List<CommunityMessEntity> communityMessEntities){
        List<ChatMessage> rawChatMessages = new ArrayList<>();
        if(communityMessEntities == null){
            return rawChatMessages;
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        for(int i =0; i < communityMessEntities.size(); i++){
            CommunityMessEntity communityMessEntity = communityMessEntities.get(i);
            if(communityMessEntity == null){
                continue;
            }
            try {
                CommunityMessage communityMessage = (CommunityMessage)gson
                        .fromJson(communityMessEntity.getMessageContent(), Class.forName(communityMessEntity.getMessageClass()));
                communityMessage.setUserSex(communityMessEntity.getSex());
                communityMessage.setCertificate(communityMessEntity.getCertificate());
                communityMessage.setNickName(communityMessEntity.getNickName());
                communityMessage.setPhoto(communityMessEntity.getPhoto());
                communityMessage.setIsSend(1);

                if(userFilter(communityMessage)){
                    rawChatMessages.add(communityMessage);
                }
            } catch (Exception e) {
                LogUtil.i(TAG,"解析历史数据失败");
            }
        }
        List<ChatMessage> chatMessages = insertDate(rawChatMessages);
        return chatMessages;
    }

    private boolean userFilter(CommunityMessage communityMessage) {
        if(!(communityMessage instanceof TagCommuMessage)){
            return true;
        }
        TagCommuMessage tagCommuMessage = (TagCommuMessage) communityMessage;
        UserFilter userFilter = tagCommuMessage.getUserFilter();
        if(userFilter == null || !userFilter.isFilterEnabled()){
            return true;
        }
        User user = SPUtil.getInstance().getUser();
        if(user == null){
            return false;
        }
        if(communityMessage.getFrom().equals(user.getUserName())){
            return true;
        }
        if(userFilter.isWhiteListEnabled() && !ArrayUtil
                .containsStringVal(userFilter.getWhiteListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isBlackListEnabled() && ArrayUtil.containsStringVal(
                userFilter.getBlackListNames(), user.getUserName())){
            return false;
        }
        if(userFilter.isAgeEnabled() && (user.getAge() < userFilter.getMinAge() || user.getAge() > userFilter.getMaxAge())){
            return false;
        }
        if(userFilter.isSexEnabled() && user.getSex() != userFilter.getSex()){
            return false;
        }
        if(userFilter.isCreditEnabled() && user.getCredit() < userFilter.getCredit()){
            return false;
        }
        if(userFilter.isCertificatEnabled() && user.getCertificate() == 0){
            return false;
        }
        return true;
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
