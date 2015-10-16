package tw.ccmos.mobi.playcamera;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by mosluce on 15/10/16.
 */
public class CropTextureView extends TextureView {
    private int mRatioWidth;
    private int mRatioHeight;
    private int mViewWidth;
    private int mViewHeight;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private FitMode mFitMode;

    public CropTextureView(Context context) {
        super(context);
    }

    public CropTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;

    }

    public void setPreviewSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mPreviewWidth = width;
        mPreviewHeight = height;

//        float scaleX = 1.0f;
//        float scaleY = 1.0f;
//
//        float realHeight = mViewWidth / width * height;
//        scaleY = realHeight / mViewHeight;
//
//        Matrix matrix = new Matrix();
//        matrix.setScale(scaleX, scaleY, 0, 0);
//
//        setTransform(matrix);
    }

    public void setFitMode(FitMode mFitMode) {
        this.mFitMode = mFitMode;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            switch (mFitMode) {
                case AUTO:
                    if (width < height * mRatioWidth / mRatioHeight) {
                        fitWidth(width);
                    } else {
                        fitHeight(height);
                    }
                    break;
                case WIDTH:
                    fitWidth(width);
                    break;
                case HIGHT:
                    fitHeight(height);
                    break;
            }
        }
    }

    private void fitHeight(int height) {
        mViewWidth = height * mRatioWidth / mRatioHeight;
        mViewHeight = height;

        if (mPreviewWidth != 0 && mPreviewHeight != 0) {
            float scale = ((float) mViewHeight) / mPreviewHeight * mPreviewWidth / mViewWidth;
            Matrix matrix = new Matrix();
            matrix.setScale(scale, 1.0f, 0, 0);
            setTransform(matrix);
        }

        setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
    }

    private void fitWidth(int width) {
        mViewWidth = width;
        mViewHeight = width * mRatioHeight / mRatioWidth;

        if (mPreviewWidth != 0 && mPreviewHeight != 0) {
            float scale = ((float) mViewWidth) / mPreviewWidth * mPreviewHeight / mViewHeight;
            Matrix matrix = new Matrix();
            matrix.setScale(1.0f, scale, 0, 0);
            setTransform(matrix);
        }

        setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }


}
