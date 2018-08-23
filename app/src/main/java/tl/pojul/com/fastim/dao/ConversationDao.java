package tl.pojul.com.fastim.dao;

import android.content.Context;

import com.pojul.fastIM.entity.Conversation;

import java.util.List;

import tl.pojul.com.fastim.dao.Util.DaoUtil;

public class ConversationDao {

    public List<Conversation> getConversations(String owner){
        String sql = "select * from conversation where conversation_owner = '" + owner +"'";
        List<Conversation> conversations = DaoUtil.executeQuery(sql, Conversation.class);
        return conversations;
    }

    public int insertConversation(Conversation conversation){
        String sql = "insert into conversation(conversation_name, conversation_from, conversation_photo" +
                ", conversation_last_chat, conversation_last_chattime, conversation_owner, unread_message, " +
                "conversation_uid, conversation_type) values(" +
                "'" + conversation.getConversationName() + "'," +
                "'" + conversation.getConversationFrom() + "'," +
                "'" + conversation.getConversationPhoto() + "'," +
                "'" + conversation.getConversationLastChat() + "'," +
                "'" + conversation.getConversationLastChattime() + "'," +
                "'" + conversation.getConversationOwner() + "'," +
                "'" + conversation.getUnreadMessage() + "'," +
                "'" + conversation.getConversionUid() + "'," +
                "'" + conversation.getConversationType() + "'" +
        ")";
        return DaoUtil.executeUpdate(sql, false);
    }

    public int updateConversationChat(Conversation conversation){
        String sql = "update conversation set " +
                "conversation_last_chat = '"+ conversation.getConversationLastChat() + "', " +
                "conversation_last_chattime = '"+ conversation.getConversationLastChattime() + "'," +
                "unread_message = '"+ conversation.getUnreadMessage() + "'" +
                " where conversation_from = '" + conversation.getConversationFrom() + "'" +
                " and conversation_owner = '" + conversation.getConversationOwner() + "'" +
                " and conversation_type = '" + conversation.getConversationType()  + "'";
        return DaoUtil.executeUpdate(sql, false);
    }

    public int updateUnreadNum(String from, String to, int num){
        String sql = "update conversation set " +
                "unread_message = '"+ num + "'" +
                " where conversation_from = '" + from + "'" +
                " and conversation_owner = '" + to + "'" +
                " and conversation_type = '" + 1 + "'";
        return DaoUtil.executeUpdate(sql, false);
    }

    public int getUnreadNum(String from, String to){
        String sql = "select * from conversation" +
                " where conversation_from = '" + from + "'" +
                " and conversation_owner = '" + to + "'" +
                " and conversation_type = '" + 1 + "'";
        List<Conversation> conversations = DaoUtil.executeQuery(sql, Conversation.class);
        if(conversations != null && conversations.size() > 0){
            return conversations.get(0).getUnreadMessage();
        }else{
            return 0;
        }
    }

    public Conversation ConversionByUid(String uid){
        String sql = "select * from conversation where conversation_uid = '" + uid + "'";
        List<Conversation> conversations = DaoUtil.executeQuery(sql, Conversation.class);
        if(conversations != null && conversations.size() > 0){
            return conversations.get(0);
        }else{
            return null;
        }
    }

    public Conversation getConversion(String from, String owner, int conversationType){
        String sql = "select * from conversation" +
                " where conversation_from = '" + from + "'" +
                "and conversation_owner = '" + owner + "' and conversation_type = " + conversationType;
        List<Conversation> conversations = DaoUtil.executeQuery(sql, Conversation.class);
        if(conversations != null && conversations.size() > 0){
            return conversations.get(0);
        }else{
            return null;
        }
    }

    public int updateReply(Conversation conversation){
        String sql = "update conversation set unread_message = " + conversation.getUnreadMessage()
                + ", conversation_name = '" +  conversation.getConversationName() + "'"
                + ", conversation_from = '" +  conversation.getConversationFrom() + "'"
                + ", conversation_photo = '" +  conversation.getConversationPhoto() + "'"
                + ", conversation_last_chat = '" +  conversation.getConversationLastChat() + "'"
                + ", conversation_last_chattime = '" +  conversation.getConversationLastChattime() + "' where conversation_uid = '" + conversation.getConversionUid() + "'";
        return DaoUtil.executeUpdate(sql, false);
    }

    public long updateUnReadNumByUid(String uid, int num){
        String sql = "update conversation set " +
                "unread_message = '"+ num + "'" +
                " where conversation_uid = '" + uid + "'" ;
        return DaoUtil.executeUpdate(sql, false);
    }

}
