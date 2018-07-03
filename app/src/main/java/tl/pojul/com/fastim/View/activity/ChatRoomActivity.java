package tl.pojul.com.fastim.View.activity;

import android.os.Bundle;
import com.pojul.fastIM.message.chat.AudioMessage;
import com.pojul.fastIM.message.chat.FileMessage;
import com.pojul.fastIM.message.chat.NetPicMessage;
import com.pojul.fastIM.message.chat.PicMessage;

public abstract class ChatRoomActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public abstract void sendPicMessage(PicMessage picMessage);

    public abstract void sendAudioMessage(AudioMessage audioMessage);

    public abstract void sendNetPicMessage(NetPicMessage netPicMessage);

    public abstract void sendSmallFileMessage(FileMessage fileMessage);

    public abstract void showSearchPic();

}
