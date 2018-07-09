package tl.pojul.com.fastim.Media;

import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.lang.ref.WeakReference;

public class VideoManager {

    private static VideoManager mVideoManager;
    private WeakReference<VideoView> videoView;
    private WeakReference<ImageView> playIcon;

    public static VideoManager getInstance() {
        if (mVideoManager == null) {
            synchronized (VideoManager.class) {
                if (mVideoManager == null) {
                    mVideoManager = new VideoManager();
                }
            }
        }
        return mVideoManager;
    }

    public void playVideo(VideoView video, ImageView play){
        if(video == null || play == null){
            return;
        }
        AudioManager.getInstance().stopPlaySound();
        videoView = new WeakReference<>(video);
        playIcon = new WeakReference<>(play);
        playIcon.get().setVisibility(View.GONE);
        videoView.get().start();
    }

    public void stopPlay(){
        if(videoView != null && videoView.get() != null){
            videoView.get().stopPlayback();
        }
        if(playIcon != null && playIcon.get() != null){
            playIcon.get().setVisibility(View.VISIBLE);
        }
    }
}
