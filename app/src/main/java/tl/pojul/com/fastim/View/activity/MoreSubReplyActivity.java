package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.pojul.fastIM.entity.MoreSubReply;
import com.pojul.fastIM.message.request.MoreSubReplyReq;
import com.pojul.fastIM.message.response.MoreSubReplyResp;
import com.pojul.objectsocket.message.ResponseMessage;
import com.pojul.objectsocket.socket.SocketRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.Adapter.MoreSubReplyAdapter;
import tl.pojul.com.fastim.util.DialogUtil;

public class MoreSubReplyActivity extends BaseActivity {

    @BindView(R.id.sub_replys)
    RecyclerView subReplys;
    @BindView(R.id.smart_refresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R.id.root_view)
    LinearLayout rootView;

    private static final int INIT = 541;

    private MoreSubReplyAdapter moreSubReplyAdapter;
    private List<MoreSubReply> mlist = new ArrayList<>();
    private long lastMilli;
    private String replyMessUid;
    private int reqNum = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_sub_reply);
        ButterKnife.bind(this);

        replyMessUid = getIntent().getStringExtra("replyMessUid");
        if (replyMessUid == null) {
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void initData() {
        subReplys.setLayoutManager(new LinearLayoutManager(this));
        moreSubReplyAdapter = new MoreSubReplyAdapter(this, mlist);
        subReplys.setAdapter(moreSubReplyAdapter);
        subReplys.setHasFixedSize(true);
        subReplys.setNestedScrollingEnabled(false);
        reqMoreSubReply(true);

        smartRefresh.setEnableLoadmore(false);
        smartRefresh.setEnableRefresh(true);
        smartRefresh.setOnRefreshListener(refreshlayout -> {
            reqMoreSubReply(false);
        });
    }

    private void reqMoreSubReply(boolean newStart) {
        if (newStart || mlist.size() <= 0) {
            lastMilli = System.currentTimeMillis();
            DialogUtil.getInstance().showLoadingSimple(this, rootView);
        }
        MoreSubReplyReq moreSubReplyReq = new MoreSubReplyReq();
        moreSubReplyReq.setReplyMessUid(replyMessUid);
        moreSubReplyReq.setNum(reqNum);
        moreSubReplyReq.setLastMilli(lastMilli);
        new SocketRequest().request(MyApplication.ClientSocket, moreSubReplyReq, new SocketRequest.IRequest() {
            @Override
            public void onError(String msg) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh();
                    showShortToas("加载失败");
                });
            }

            @Override
            public void onFinished(ResponseMessage mResponse) {
                runOnUiThread(() -> {
                    DialogUtil.getInstance().dimissLoadingDialog();
                    smartRefresh.finishRefresh();
                    List<MoreSubReply> tempMore = ((MoreSubReplyResp) mResponse).getMoreSubReplies();
                    if (tempMore.size() <= 0) {
                        smartRefresh.setEnableRefresh(false);
                        return;
                    }
                    Collections.reverse(tempMore);
                    lastMilli = tempMore.get(0).getTimeMilli();
                    if (tempMore.size() < reqNum) {
                        smartRefresh.setEnableRefresh(false);
                    }
                    moreSubReplyAdapter.addData(tempMore);
                });
            }
        });

    }


    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<MoreSubReplyActivity> activity;

        MyHandler(MoreSubReplyActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().initData();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        DialogUtil.getInstance().dimissLoadingDialog();
        super.onDestroy();
    }
}
