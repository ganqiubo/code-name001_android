package tl.pojul.com.fastim.View.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.message.chat.ChatMessage;
import com.pojul.fastIM.message.chat.TextChatMessage;
import com.pojul.fastIM.message.request.GetConversionInfoRequest;
import com.pojul.fastIM.message.response.GetConversionInfoResponse;
import com.pojul.objectsocket.message.BaseMessage;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.PolygonImage.view.PolygonImageView;
import tl.pojul.com.fastim.dao.ConversationDao;
import tl.pojul.com.fastim.util.DateUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Conversation> mList;

    public ConversationAdapter(Context mContext, List<Conversation> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversations_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Conversation conversation = mList.get(position);

        if (conversation.getConversationPhoto() != null) {
            Glide.with(mContext).load(conversation.getConversationPhoto()).into(holder.conversationPhoto);
        } else {
            Glide.with(mContext).load(R.drawable.photo_default).into(holder.conversationPhoto);
        }
        holder.conversationName.setText(conversation.getConversationName());
        holder.conversationLastChat.setText(conversation.getConversationLastChat());
        holder.conversationLastChattime.setText(DateUtil.getConversationDate(conversation.getConversationLastChattime()));
        if (conversation.getUnreadMessage() > 0) {
            holder.unreadMessage.setVisibility(View.VISIBLE);
            holder.unreadMessage.setText(("" + conversation.getUnreadMessage()));
        } else {
            holder.unreadMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.conversation_photo)
        PolygonImageView conversationPhoto;
        @BindView(R.id.conversation_name)
        TextView conversationName;
        @BindView(R.id.conversation_last_chat)
        TextView conversationLastChat;
        @BindView(R.id.conversation_last_chattime)
        TextView conversationLastChattime;
        @BindView(R.id.unread_message)
        TextView unreadMessage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void receiveMessage(BaseMessage message){
        if(mList == null){
            return;
        }
        boolean containConversation = false;
        for(int i = 0; i< mList.size(); i ++ ){
            Conversation conversation = mList.get(i);
            if(conversation.getConversationFrom().equals(message.getFrom())){
                conversation.setUnreadMessage(conversation.getUnreadMessage() + 1);
                if(message instanceof TextChatMessage){
                    conversation.setConversationLastChat(((TextChatMessage)message).getText());
                }else{
                    conversation.setConversationLastChat("非文字消息");
                }
                conversation.setConversationLastChattime(message.getSendTime());
                new ConversationDao().updateConversationChat(conversation);
                this.notifyDataSetChanged();
                containConversation = true;
                break;
            }
        }
        if(!containConversation){
            if(message instanceof ChatMessage){
                requestConversationInfo((ChatMessage)message);
            }else{
            }
        }
    }

    private void requestConversationInfo(ChatMessage message) {

        Conversation conversation = new Conversation();
        conversation.setUnreadMessage(1);
        conversation.setConversationFrom(message.getFrom());
        if(message instanceof TextChatMessage){
            conversation.setConversationLastChat(((TextChatMessage)message).getText());
        }else{
            conversation.setConversationLastChat("非文字消息");
        }
        conversation.setConversationLastChattime(message.getSendTime());
        conversation.setConversationOwner(SPUtil.getInstance().getUser().getUserName());

        GetConversionInfoRequest request = new GetConversionInfoRequest();
        request.setRequestUrl("GetConversionInfo");
        request.setChatRoomType(message.getChatType());
        request.setConversionFrom(message.getFrom());
        request.setOwner(SPUtil.getInstance().getUser().getUserName());
        new SocketRequest().resuest(MyApplication.ClientSocket, request, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                updatePhotoName(conversation, null, "?不存在?");
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                new Handler(Looper.getMainLooper()).post(()->{
                    GetConversionInfoResponse response = (GetConversionInfoResponse) mResponse;
                    if (response.getCode() == 200) {
                        updatePhotoName(conversation,
                                response.getConversation().getConversationPhoto(),
                                response.getConversation().getConversationName());

                    }else{
                        updatePhotoName(conversation, null, "?不存在?");
                    }
                });
            }
        });
    }

    public void updatePhotoName(Conversation conversation, String photo, String name){
        conversation.setConversationPhoto(photo);
        conversation.setConversationName(name);
        new ConversationDao().insertConversation(conversation);
        mList.add(conversation);
        ConversationAdapter.this.notifyDataSetChanged();
    }

}
