package tl.pojul.com.fastim.View.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.RightAlphaInTransit;

public class MySeekBar extends View {
    private Paint paint = new Paint();

    private int lineTop, lineBottom, lineLeft, lineRight;
    private int lineCorners;
    private int lineWidth;
    private RectF line = new RectF();

    private int colorLineSelected;
    private int colorLineEdge;

    private SeekBar rightSB = new SeekBar();
    private boolean currTouch = false;

    private OnRangeChangedListener callback;

    private int seekBarResId;
    private int cellsCount = 1;
    private int max;

    private class SeekBar {
        RadialGradient shadowGradient;
        Paint defaultPaint;
        int lineWidth;
        int widthSize, heightSize;
        float currPercent = 0;
        int left, right, top, bottom;
        Bitmap bmp;

        float material = 0;
        ValueAnimator anim;
        final TypeEvaluator<Integer> te = new TypeEvaluator<Integer>() {
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int alpha = (int) (Color.alpha(startValue) + fraction * (Color.alpha(endValue) - Color.alpha(startValue)));
                int red = (int) (Color.red(startValue) + fraction * (Color.red(endValue) - Color.red(startValue)));
                int green = (int) (Color.green(startValue) + fraction * (Color.green(endValue) - Color.green(startValue)));
                int blue = (int) (Color.blue(startValue) + fraction * (Color.blue(endValue) - Color.blue(startValue)));
                return Color.argb(alpha, red, green, blue);
            }
        };

        void onSizeChanged(int centerX, int centerY, int hSize, int parentLineWidth, boolean cellsMode, int bmpResId, Context context) {
            heightSize = hSize;
            widthSize = (int) (heightSize * 0.8f);
            left = centerX - widthSize / 2;
            right = centerX + widthSize / 2;
            top = centerY - heightSize / 2;
            bottom = centerY + heightSize / 2;

            if (cellsMode) {
                lineWidth = parentLineWidth;
            } else {
                lineWidth = parentLineWidth - widthSize;
            }

            if (bmpResId > 0) {
                Bitmap original = BitmapFactory.decodeResource(context.getResources(), bmpResId);
                Matrix matrix = new Matrix();
                float scaleWidth = ((float) widthSize) / original.getWidth();
                float scaleHeight = ((float) heightSize) / original.getHeight();
                matrix.postScale(scaleWidth, scaleHeight);
                bmp = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
            } else {
                defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                int radius = (int) (widthSize * 0.5f);
                int barShadowRadius = (int) (radius * 0.95f);
                int mShadowCenterX = widthSize / 2;
                int mShadowCenterY = heightSize / 2;
                shadowGradient = new RadialGradient(mShadowCenterX, mShadowCenterY, barShadowRadius, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
            }
        }

        boolean collide(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            int offset = (int) ((lineWidth + widthSize) * currPercent);
            return x > left + offset && x < right + offset && y > top && y < bottom;
        }

        void slide(float percent) {
            if (percent < 0) percent = 0;
            else if (percent > 1) percent = 1;
            currPercent = percent;
        }

        private void materialRestore() {
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(material, 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    material = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    material = 0;
                    invalidate();
                }
            });
            anim.start();
        }

        void draw(Canvas canvas) {
            int offset = (int) ((lineWidth + widthSize) * currPercent);
            canvas.save();
            canvas.translate(offset, 0);
            if (bmp != null) {
                canvas.drawBitmap(bmp, left, top, null);
            } else {
                canvas.translate(left, 0);
                drawDefault(canvas);
            }
            canvas.restore();
        }

        private void drawDefault(Canvas canvas) {
            int centerX = widthSize / 2;
            int centerY = heightSize / 2;
            int radius = (int) (widthSize * 0.5f);
            // draw shadow
            defaultPaint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.translate(0, radius * 0.25f);
            canvas.scale(1 + (0.1f * material), 1 + (0.1f * material), centerX, centerY);
            defaultPaint.setShader(shadowGradient);
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
            defaultPaint.setShader(null);
            canvas.restore();
            // draw body
            defaultPaint.setStyle(Paint.Style.FILL);
            defaultPaint.setColor(te.evaluate(material, 0xFFFFFFFF, 0xFFE7E7E7));
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
            // draw border
            defaultPaint.setStyle(Paint.Style.STROKE);
            defaultPaint.setColor(0xFFD7D7D7);
            canvas.drawCircle(centerX, centerY, radius, defaultPaint);
        }
    }

    public interface OnRangeChangedListener {
        void onRangeChanged(MySeekBar view, float progress);
    }

    public MySeekBar(Context context) {
        this(context, null);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        seekBarResId = t.getResourceId(R.styleable.RangeSeekBar_seekBarResId, 0);
        colorLineSelected = t.getColor(R.styleable.RangeSeekBar_lineColorSelected, 0xFF4BD962);
        colorLineEdge = t.getColor(R.styleable.RangeSeekBar_lineColorEdge, 0xFFD7D7D7);
        max = (int) t.getFloat(R.styleable.RangeSeekBar_max, 1);
        t.recycle();
    }

    public void setOnRangeChangedListener(OnRangeChangedListener listener) {
        callback = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize * 1.8f > widthSize) {
            setMeasuredDimension(widthSize, (int) (widthSize / 1.8f));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int seekBarRadius = h / 2;

        lineLeft = seekBarRadius;
        lineRight = w - seekBarRadius;
        lineTop = seekBarRadius - seekBarRadius / 6;
        lineBottom = seekBarRadius + seekBarRadius / 6;
        lineWidth = lineRight - lineLeft;
        line.set(lineLeft, lineTop, lineRight, lineBottom);
        lineCorners = (int) ((lineBottom - lineTop) * 0.45f);

        rightSB.onSizeChanged(seekBarRadius, seekBarRadius, h, lineWidth, cellsCount > 1, seekBarResId, getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorLineEdge);
        canvas.drawRoundRect(line, lineCorners, lineCorners, paint);
        paint.setColor(colorLineSelected);
        RectF lineSelect = new RectF(lineLeft, lineTop,
                rightSB.left + rightSB.widthSize / 2 + (rightSB.lineWidth + rightSB.widthSize) * rightSB.currPercent, lineBottom);
        canvas.drawRoundRect(lineSelect, lineCorners, lineCorners, paint);
        rightSB.draw(canvas);
    }

    public void setValue(int value){
        float percent = value * 1.0f / max;
        rightSB.slide(percent);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MySeekBar.this.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currTouch = rightSB.collide(event);
                return currTouch;
            case MotionEvent.ACTION_MOVE:
                float percent;
                float x = event.getX();
                rightSB.material = rightSB.material >= 1 ? 1 : rightSB.material + 0.1f;

                if (x > lineRight) {
                    percent = 1;
                } else {
                    percent = x * 1f / (lineWidth + rightSB.widthSize);
                }

                if(currTouch){
                    rightSB.slide(percent);
                    if(callback != null){
                        int progress = (int) (max * percent);
                        if(progress > max){
                            progress = max;
                        }else if(progress < 0){
                            progress = 0;
                        }
                        callback.onRangeChanged(MySeekBar.this, progress);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                currTouch = false;
                rightSB.materialRestore();
                break;
        }
        return super.onTouchEvent(event);
    }
}
