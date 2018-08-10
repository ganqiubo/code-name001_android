package tl.pojul.com.fastim.View.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pojul.fastIM.message.chat.CommunityMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.objectsocket.constant.FileType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tl.pojul.com.fastim.Factory.ChatMessageFcctory;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.ChatRoomActivity;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.http.converter.BaiduPicConverter;
import tl.pojul.com.fastim.http.request.HttpRequestManager;
import tl.pojul.com.fastim.util.DensityUtil;
import tl.pojul.com.fastim.util.DialogUtil;

public class SearchPicAdapter extends RecyclerView.Adapter<SearchPicAdapter.ViewHolder> {

    private Context mContext;
    private List<NetPicMessage> mList;
    private int itemWidth;
    private HashMap<Integer, Integer> itemHeight = new HashMap<>();
    private int chatRoomType;

    private String searchEngine = "baidu";
    private boolean isSearch;

    public SearchPicAdapter(Context mContext, List<NetPicMessage> mList, int chatRoomType) {
        this.mContext = mContext;
        this.mList = mList;
        this.chatRoomType = chatRoomType;
        itemWidth = (int) ((MyApplication.SCREEN_WIDTH - DensityUtil.dp2px(mContext,12))* 1.0f/3);
    }

    @Override
    public SearchPicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_netpic, parent, false);
        return new SearchPicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchPicAdapter.ViewHolder holder, int position) {
        NetPicMessage netPicMessage = mList.get(position);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.pic.getLayoutParams();
        float heightWidthRate = 0;
        if(netPicMessage.getWidth() != 0){
            heightWidthRate = netPicMessage.getHeight() *1.0f / netPicMessage.getWidth();
        }

        if(!itemHeight.containsKey(position)){
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (itemWidth * heightWidthRate);
            itemHeight.put(position, layoutParams.height);
        }else{
            layoutParams.width = itemWidth;
            layoutParams.height = itemHeight.get(position);
        }

        holder.pic.setLayoutParams(layoutParams);

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.pic)
                .error(R.drawable.pic)
                .fallback(R.drawable.pic);
        Glide.with(mContext).load(netPicMessage.getThumbURL().getFilePath()).apply(options).into(holder.pic);
        holder.view.setOnClickListener(v->{
            try{
                DialogUtil.getInstance().showDetailImgDialogPop(mContext, netPicMessage,
                       holder.pic, DialogUtil.POP_TYPR_IMG);
            }catch(Exception e){}
        });
        holder.pic.setOnClickListener(v->{
            try{
                DialogUtil.getInstance().showDetailImgDialogPop(mContext, netPicMessage,
                        holder.pic, DialogUtil.POP_TYPR_IMG);
            }catch(Exception e){}
        });
        holder.send.setOnClickListener(v->{
            if (chatRoomType == 3){
                CommunityMessage communityMessage = new ChatMessageFcctory().createCommunityMessage(netPicMessage);
                ((ChatRoomActivity)mContext).sendChatMessage(communityMessage);
            }else{
                netPicMessage.setChatType(chatRoomType);
                ((ChatRoomActivity)mContext).sendChatMessage(netPicMessage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void clearData() {
        synchronized (mList){
            itemHeight.clear();
            mList.removeAll(mList);
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.pic)
        ImageView pic;
        @BindView(R.id.view)
        ImageView view;
        @BindView(R.id.send)
        ImageView send;
        @BindView(R.id.rl_operation)
        LinearLayout rlOperation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void searchNetPic(String keyWord) {
        if(isSearch){
           return;
        }
        isSearch= true;
        HttpRequestManager.getInstance().getNetPicSearchReq(keyWord, searchEngine, mList.size()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((Activity)mContext).runOnUiThread(()-> {
                    showShortToas("加载失败");
                });
                isSearch = false;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                isSearch = false;
                ((Activity)mContext).runOnUiThread(()-> {
                    ArrayList<NetPicMessage> netPics = null;
                    switch (searchEngine){
                        case "baidu":
                            netPics = new BaiduPicConverter().converter(responseStr);
                            break;
                    }
                    if(netPics != null){
                        if(netPics.size() <= 0){
                            showShortToas("已经到底了");
                            return;
                        }
                        synchronized (mList){
                            int position = mList.size();
                            mList.addAll(netPics);
                            SearchPicAdapter.this.notifyItemRangeInserted(position, netPics.size());
                        }
                    }
                });
            }
        });
    }

    public void showShortToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLongToas(String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
