package tl.pojul.com.fastim.dao;

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
                ", conversation_last_chat, conversation_last_chattime, conversation_owner, unread_message) values(" +
                "'" + conversation.getConversationName() + "'," +
                "'" + conversation.getConversationFrom() + "'," +
                "'" + conversation.getConversationPhoto() + "'," +
                "'" + conversation.getConversationLastChat() + "'," +
                "'" + conversation.getConversationLastChattime() + "'," +
                "'" + conversation.getConversationOwner() + "'," +
                "'" + conversation.getUnreadMessage() + "'" +
        ")";
        return DaoUtil.executeUpdate(sql);
    }

    public int deleteConversation(String from, String owner){
        String sql = "delete from conversation where conversation_from = '" + from + "'" +
                "and conversation_owner = '" + owner + "'";
        return DaoUtil.executeUpdate(sql);
    }

    public int updateConversationChat(Conversation conversation ){
        String sql = "update conversation set " +
                "conversation_last_chat = '"+ conversation.getConversationLastChat() + "', " +
                "conversation_last_chattime = '"+ conversation.getConversationLastChattime() + "'," +
                "unread_message = '"+ conversation.getUnreadMessage() + "'" +
                " where conversation_from = '" + conversation.getConversationFrom() + "'" +
                "and conversation_owner = '" + conversation.getConversationOwner() + "'";
        return DaoUtil.executeUpdate(sql);
    }

}
