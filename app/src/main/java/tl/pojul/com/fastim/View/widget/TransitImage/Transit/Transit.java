package tl.pojul.com.fastim.View.widget.TransitImage.Transit;

public class Transit{

    /**
     *从右往左淡入
     */
    public static final int RIGHT_ALPHA_IN = 1;

    /**
     *从左往右移出
     */
    public static final int LEFT_SLOW_OUT = 2;

    /**
     *扇形切入
     */
    public static final int SECTOR_CLIP_IN= 3;

    public int during = 1000;

    protected int transitType = 1;

    public int getTransitType() {
        return transitType;
    }

    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }
}
