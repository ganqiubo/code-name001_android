package tl.pojul.com.fastim.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.fastIM.message.other.NotifyReplyMess;
import com.pojul.fastIM.message.chat.PicMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.request.GetConversionInfoRequest;
import com.pojul.fastIM.message.response.GetConversionInfoResponse;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.dao.ConversationDao;

public class ConversationUtil {

    public static String getNoteText(BaseMessage message) {
        if (message instanceof TextChatMessage) {
            return ((TextChatMessage) message).getText();
        } else if (message instanceof PicMessage || message instanceof NetPicMessage){
            return "图片";
        } else if (message instanceof AudioMessage){
            return "语音";
        } else if (message instanceof FileMessage){
            return "文件";
        } else {
            return "非文字消息";
        }
    }

    public static void addFriendConversionAndNotify(ChatMessage message, Context context){
            Conversation conversation = new Conversation();
            conversation.setUnreadMessage(1);
            conversation.setConversationFrom(message.getFrom());
            conversation.setConversationLastChat(ConversationUtil.getNoteText(message));
            conversation.setConversationLastChattime(message.getSendTime());
            conversation.setConversationOwner(SPUtil.getInstance().getUser().getUserName());
            conversation.setConversationType(message.getChatType());

            GetConversionInfoRequest request = new GetConversionInfoRequest();
            request.setRequestUrl("GetConversionInfo");
            request.setChatRoomType(message.getChatType());
            request.setConversionFrom(message.getFrom());
            request.setOwner(SPUtil.getInstance().getUser().getUserName());
            new SocketRequest().request(MyApplication.ClientSocket, request, new SocketRequest.IRequest() {
                @Override
                public void onError(String msg) {
                    updatePhotoName(conversation, null, "?不存在?", context);
                }

                @Override
                public void onFinished(ResponseMessage mResponse) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        GetConversionInfoResponse response = (GetConversionInfoResponse) mResponse;
                        if (response.getCode() == 200) {
                            updatePhotoName(conversation,
                                    response.getConversation().getConversationPhoto(),
                                    response.getConversation().getConversationName(),
                                    context);
                        } else {
                            updatePhotoName(conversation, null, "?不存在?", context);
                        }
                    });
                }
            });
    }

    public static void updateConversionAndNotify(ChatMessage message, Context context, Conversation conversation){
        ConversationDao conversationDao = new ConversationDao();
        conversation.setConversationLastChat(ConversationUtil.getNoteText(message));
        conversation.setConversationLastChattime(DateUtil.getFormatDate());
        int getUnreadNum = conversationDao.getUnreadNum(message.getFrom(),  SPUtil.getInstance().getUser().getUserName());
        conversation.setUnreadMessage((getUnreadNum + 1));
        conversationDao.updateConversationChat(conversation);
        NotifyUtil.notifyChatMess(conversation, context);
    }

    public static void updatePhotoName(Conversation conversation, String photo, String name, Context context) {
        conversation.setConversationPhoto(photo);
        conversation.setConversationName(name);
        new ConversationDao().insertConversation(conversation);
        NotifyUtil.notifyChatMess(conversation, context);
    }

    public static void onRecMessWhileBack(BaseMessage message, Context context){
        ConversationDao conversationDao = new ConversationDao();
        Conversation conversation;
        if(message instanceof ChatMessage){
            conversation = conversationDao.getConversion(message.getFrom(),
                    SPUtil.getInstance().getUser().getUserName(), ((ChatMessage)message).getChatType());
            if (conversation != null) {
                updateConversionAndNotify((ChatMessage) message, context, conversation);
            } else {
                addFriendConversionAndNotify((ChatMessage) message, context);
            }
        }else if(message instanceof NotifyReplyMess){
            conversation = conversationDao.ConversionByUid(((NotifyReplyMess)message).getReplyTagMessUid());
            if(conversation == null){
                conversation = insertNotifyReply((NotifyReplyMess)message, conversationDao);
                NotifyUtil.notifyChatMess(conversation, context);
            }else{
                updateNotifyReply((NotifyReplyMess) message, conversationDao, conversation);
                NotifyUtil.notifyChatMess(conversation, context);
            }
        }
    }

    public static Conversation updateNotifyReply(NotifyReplyMess message, ConversationDao conversationDao, Conversation conversation) {
        if(conversation.getConversationType() == 3){
            if(message.getUnSendCount() <= 0){
                conversation.setUnreadMessage((conversation.getUnreadMessage() + 1));
            }else{
                conversation.setUnreadMessage((conversation.getUnreadMessage() + message.getUnSendCount()));
            }
            conversation.setConversationName(message.getReplyNickName() + "回复了你\"" + message.getTagMessTitle() + "\"");
            conversation.setConversationFrom(message.getFrom());
            conversation.setConversationPhoto(message.getPhoto());
            conversation.setConversationLastChat(message.getReplyText());
            conversation.setConversationLastChattime(message.getSendTime());
            conversationDao.updateReply(conversation);
        }else{

        }
        return conversation;
    }

    public static Conversation insertNotifyReply(NotifyReplyMess message, ConversationDao conversationDao) {
        Conversation conversation = new Conversation();
        if(message.getReplyType() == 0){
            conversation.setConversationName(message.getReplyNickName() + "回复了你\"" + message.getTagMessTitle() + "\"");
            conversation.setConversationFrom(message.getFrom());
            conversation.setConversationPhoto(message.getPhoto());
            conversation.setConversationLastChat(message.getReplyText());
            conversation.setConversationLastChattime(message.getSendTime());
            conversation.setConversationOwner(message.getTo());
            if(message.getUnSendCount() <= 0){
                conversation.setUnreadMessage(1);
            }else{
                conversation.setUnreadMessage(message.getUnSendCount());
            }
            conversation.setConversationType(3);
            conversation.setConversionUid(message.getReplyTagMessUid());
            conversationDao.insertConversation(conversation);
        }else{

        }
        return conversation;
    }

}
