package tl.pojul.com.fastim.socket.Converter;

import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.chat.SubReplyMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SubReplyConverter {

    public List<SubReplyMessage> converter(ReplyMessage replyMessage, String strs){
        if(strs == null || strs.isEmpty() || "NULL".equals(strs) || "null".equals(strs)){
            return null;
        }
        List<SubReplyMessage> subReplyMessages = new ArrayList<>();
        String[] strList = strs.split(";");
        for (int i = 0; i < strList.length; i++) {
            if(i > 7){
                replyMessage.setHasMoreSubReply(true);
                break;
            }
            String[] itemStrs = strList[i].split(",");
            if(itemStrs.length != 8){
                continue;
            }
            SubReplyMessage subReplyMessage = new SubReplyMessage();
            subReplyMessage.setReplyMessageUid(itemStrs[0]);
            subReplyMessage.setUserName(itemStrs[1]);
            subReplyMessage.setFrom(itemStrs[1]);
            subReplyMessage.setNickName(itemStrs[2]);
            subReplyMessage.setMessageUid(itemStrs[3]);
            try{
                subReplyMessage.setIsSpaceTravel(Integer.parseInt(itemStrs[4]));
                subReplyMessage.setTimeMilli(Long.parseLong(itemStrs[6]));
            }catch (Exception e){}
            subReplyMessage.setText(itemStrs[5]);
            subReplyMessage.setReplyTagMessUid(itemStrs[7]);
            subReplyMessage.setIsSend(1);
            subReplyMessages.add(subReplyMessage);
        }
        Collections.reverse(subReplyMessages);
        return subReplyMessages;
    }

}
