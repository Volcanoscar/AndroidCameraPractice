package tw.ccmos.mobi.playcamera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by mosluce on 15/10/15.
 */
public class AutoFitLayout extends FrameLayout {
    int mRatioWidth;
    int mRatioHeight;

    AutoFitMode autoFitMode = AutoFitMode.AUTO;

    public AutoFitLayout(Context context) {
        super(context);
    }

    public AutoFitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;

        requestLayout();
    }

    public void setAutoFitMode(AutoFitMode autoFitMode) {
        this.autoFitMode = autoFitMode;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            switch (autoFitMode) {
                case AUTO:
                    fit(width, height);
                    break;
                case WIDTH:
                    fitWidth(width);
                    break;
                case HEIGHT:
                    fitHeight(height);
                    break;
            }
        }
    }

    private void fitHeight(int height) {
        setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
    }

    private void fitWidth(int width) {
        setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
    }

    private void fit(int width, int height) {
        if (width < height * mRatioWidth / mRatioHeight) {
            setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
        } else {
            setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
        }
    }
}
