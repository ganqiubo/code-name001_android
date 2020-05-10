package tl.pojul.com.fastim.View.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.Friend;
import com.pojul.fastIM.entity.User;
import com.pojul.fastIM.message.request.ChatLegalReq;
import com.pojul.fastIM.message.request.GetFriendsRequest;
import com.pojul.fastIM.message.response.ChatLegalResp;
import com.pojul.fastIM.message.response.GetFriendsResponse;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.FriendsAdapter;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.util.DialogUtil;
import tl.pojul.com.fastim.util.SPUtil;

public class FriendsFragment extends BaseFragment {

    @BindView(R.id.friends_list)
    SwipeMenuRecyclerView friendsList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    private Unbinder unbinder;
    public FriendsAdapter friendsAdapter;
    private View view;
    @BindView(R.id.empty_ll)
    LinearLayout emptyLl;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view!=null){
            ViewGroup parent =(ViewGroup)view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        friendsList.setLayoutManager(layoutmanager);

        friendsList.setSwipeMenuCreator(swipeMenuCreator);
        friendsList.setSwipeMenuItemClickListener(mMenuItemClickListener);
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getFriends(false);
            }
        });
        getFriends(true);
    }

    public void getFriends(boolean showDialog) {
        if (!MyApplication.getApplication().isConnected()) {
            smartRefresh.finishRefresh(false);
            showShortToas("请重新连接服务器");
            return;
        }
        GetFriendsRequest getFriendsRequest = new GetFriendsRequest();
        getFriendsRequest.setRequestUrl("GetFriends");
        getFriendsRequest.setUserName(SPUtil.getInstance().getUser().getUserName());
        if(showDialog){
            DialogUtil.getInstance().showLoadingDialog(getActivity(), "加载中...");
        }
        new SocketRequest().request(MyApplication.ClientSocket, getFriendsRequest, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                getActivity().runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(false);
                    showShortToas(msg);
                    emptyLl.setVisibility(View.VISIBLE);
                    friendsList.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                getActivity().runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh(true);
                    GetFriendsResponse getFriendsResponse = (GetFriendsResponse) mResponse;
                    if (mResponse.getCode() == 200) {
                        if (getFriendsResponse.getFriends() != null && getFriendsResponse.getFriends().size() > 0) {
                            friendsAdapter = new FriendsAdapter(getActivity(), getFriendsResponse.getFriends());
                            friendsAdapter.setOnItemClickListener(new ItemClickListener());
                            friendsList.setAdapter(friendsAdapter);
                            refreshUnreadNum();
                            if(getFriendsResponse.getFriends() == null || getFriendsResponse.getFriends().size() <= 0){
                                emptyLl.setVisibility(View.VISIBLE);
                                friendsList.setVisibility(View.GONE);
                            }else{
                                emptyLl.setVisibility(View.GONE);
                                friendsList.setVisibility(View.VISIBLE);
                            }
                        } else {
                            emptyLl.setVisibility(View.VISIBLE);
                            friendsList.setVisibility(View.GONE);
                        }
                    } else {
                        //showShortToas(mResponse.getMessage());
                        emptyLl.setVisibility(View.VISIBLE);
                        friendsList.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void refreshUnreadNum(){
        MainActivity mainActivity = (MainActivity)getActivity();
        ConversationFragment conversationFragment = null;
        if(mainActivity.chatFragment != null){
            conversationFragment = mainActivity.chatFragment.conversationFragment;
        }
        if(conversationFragment != null && conversationFragment.conversationAdapter != null){
            conversationFragment.conversationAdapter.notifyUnReadNum();
        }
    }

    class ItemClickListener implements FriendsAdapter.OnItemClickListener {
        @Override
        public void onClick(int position) {
            User user = SPUtil.getInstance().getUser();
            Friend friend = friendsAdapter.getmList().get(position);
            if (friendsAdapter == null || friendsAdapter.getmList() == null || user == null || friend == null){
                return;
            }
            ChatLegalReq req = new ChatLegalReq();
            req.setUserNameOwn(user.getUserName());
            req.setUserNameFriend(friend.getUserName());
            new SocketRequest().request(MyApplication.ClientSocket, req, new SocketRequest.IRequest() {
                @Override
                public void onError(String msg) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinished(ResponseMessage mResponse) {
                    new Handler(Looper.getMainLooper()).post(()->{
                        if(mResponse.getCode() == 200){
                            ChatLegalResp resp = (ChatLegalResp) mResponse;
                            if(resp.getLegal() == 3){
                                Toast.makeText(getActivity(), "你和TA还不是好友关系，不能直接聊天", Toast.LENGTH_SHORT).show();
                            }else{
                                if(resp.getUser() == null){
                                    Toast.makeText(getActivity(), "数据错误", Toast.LENGTH_SHORT).show();
                                }else{
                                    Intent intent = new Intent(getActivity(), SingleChatRoomActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("chat_room_type", 1);
                                    bundle.putString("chat_room_name", friend.getNickName());
                                    bundle.putString("friend", new Gson().toJson(friend));
                                    intent.putExtras(bundle);
                                    getActivity().startActivity(intent);
                                }
                            }
                        }else{
                            Toast.makeText(getActivity(), mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

            int width = getResources().getDimensionPixelSize(R.dimen.dp_100);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                    .setBackground(R.drawable.selector_red)
                    .setImage(R.drawable.ic_action_delete) // 图标。
                    .setText("删除") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.

            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackground(R.drawable.selector_yellow)
                    .setImage(R.drawable.ic_action_close)
                    .setWidth((int)(width * 0.95f))
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加菜单到右侧。
            // 上面的菜单哪边不要菜单就不要添加。
        }
    };

    /**
     * RecyclerView的Item中的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                //Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
                //showLongToas("list第" + adapterPosition + "; 右侧菜单第" + menuPosition);
                if(menuPosition == 0){
                    DialogUtil.getInstance().showPromptDialog(getActivity(), "删除好友", "确定删除该好友？");
                    DialogUtil.getInstance().setDialogClick(str -> {
                        if("确定".equals(str)){
                            friendsAdapter.deleteItem(adapterPosition);
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
