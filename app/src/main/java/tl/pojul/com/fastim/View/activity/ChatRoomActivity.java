package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;

import tl.pojul.com.fastim.R;

public class ChatRoomActivity extends BaseActivity {

    private String chatRoomName;
    private String chat_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
    }
}
