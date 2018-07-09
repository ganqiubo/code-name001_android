package tl.pojul.com.fastim.View.activity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.pojul.fastIM.message.chat.VideoMessage;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.MyVideoView;

public class VideoActivity extends BaseActivity {

    private static final int INIT = 1001;
    @BindView(R.id.video)
    MyVideoView video;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.play)
    ImageView play;
    @BindView(R.id.process)
    ProgressBar process;
    private VideoMessage videoMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoMessage = new Gson().fromJson(getIntent().getStringExtra("videoMessage"), VideoMessage.class);
        if (videoMessage == null) {
            finish();
            return;
        }
        if (videoMessage.getVideoWidth() > videoMessage.getVideoHeight()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        if(videoMessage.getFirstPic() != null){
            img.setVisibility(View.VISIBLE);
            if (!FileClassUtil.isHttpUrl(videoMessage.getFirstPic().getFilePath())) {
                File file = new File(videoMessage.getFirstPic().getFilePath());
                Glide.with(this).load(file).into(img);
            } else {
                Glide.with(this).load(videoMessage.getFirstPic().getFilePath()).into(img);
            }
        }
        process.setVisibility(View.VISIBLE);
        play.setVisibility(View.GONE);

        mHandler.sendEmptyMessageDelayed(INIT, 600);

    }

    private void init() {
        video.setVideoPath(videoMessage.getVideo().getFilePath());
        Log.e("VideoActivity", "" + videoMessage.getVideo().getFilePath());
        MediaController mediaController = new MediaController(this);
        video.setMediaController(mediaController);
        mediaController.setMediaPlayer(video);
        mediaController.show();
        video.start();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                img.setVisibility(View.GONE);
                process.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video.seekTo(0);
                play.setVisibility(View.VISIBLE);
            }
        });

        video.setPlayPauseListener(new MyVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                play.setVisibility(View.GONE);
            }

            @Override
            public void onPause() {
                play.setVisibility(View.VISIBLE);
            }
        });

        play.setOnClickListener(view->{
            play.setVisibility(View.GONE);
            video.start();
        });

    }

    private MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<VideoActivity> activity;

        MyHandler(VideoActivity activity) {
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
                    activity.get().init();
                    break;
            }
        }
    }


}
