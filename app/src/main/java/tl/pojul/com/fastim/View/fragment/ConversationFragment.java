package tl.pojul.com.fastim.View.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pojul.objectsocket.message.BaseMessage;
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
import tl.pojul.com.fastim.View.Adapter.ConversationAdapter;
import tl.pojul.com.fastim.View.activity.SingleChatRoomActivity;
import tl.pojul.com.fastim.dao.ConversationDao;
import tl.pojul.com.fastim.util.SPUtil;

public class ConversationFragment extends BaseFragment {

    @BindView(R.id.conversation_list)
    SwipeMenuRecyclerView conversationList;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    Unbinder unbinder;

    public ConversationAdapter conversationAdapter;
    private View view;

    public ConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view!=null){
            ViewGroup parent =(ViewGroup)view.getParent();
            parent.removeView(view);
        }
        view = inflater.inflate(R.layout.fragment_conversation, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        conversationList.setLayoutManager(layoutmanager);
        conversationList.setSwipeMenuCreator(swipeMenuCreator);
        conversationList.setSwipeMenuItemClickListener(mMenuItemClickListener);

        conversationAdapter = new ConversationAdapter(getActivity(),
                new ConversationDao().getConversations(SPUtil.getInstance().getUser().getUserName()));
        conversationAdapter.setOnItemClickListener(new ItemClickListener());
        conversationList.setAdapter(conversationAdapter);

        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshList();
                smartRefresh.finishRefresh();
            }
        });

        MyApplication.getApplication().registerReceiveMessage(iReceiveMessage);

    }

    class ItemClickListener implements ConversationAdapter.OnItemClickListener {
        @Override
        public void onClick(int position) {
            Intent intent = new Intent(getActivity(), SingleChatRoomActivity.class);
            startActivity(intent);
        }
    };

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
                showLongToas("list第" + adapterPosition + "; 右侧菜单第" + menuPosition);
            }
        }
    };

    private MyApplication.IReceiveMessage iReceiveMessage = new MyApplication.IReceiveMessage() {
        @Override
        public void receiveMessage(BaseMessage message) {
            showLongToas("iReceiveMessage--->" + message.getFrom());
            if(conversationAdapter != null){
                conversationAdapter.receiveMessage(message);
            }
        }
    };

    public void refreshList(){
        conversationAdapter = new ConversationAdapter(getActivity(),
                new ConversationDao().getConversations(SPUtil.getInstance().getUser().getUserName()));
        conversationAdapter.setOnItemClickListener(new ItemClickListener());
        conversationList.setAdapter(conversationAdapter);
        conversationAdapter.notifyUnReadNum();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //showShortToas("isVisibleToUser: " + isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        //showShortToas("onResume");
        refreshList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        MyApplication.getApplication().unRegisterReceiveMessage(iReceiveMessage);
    }
}
