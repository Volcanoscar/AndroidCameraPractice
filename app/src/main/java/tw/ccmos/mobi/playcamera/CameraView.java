package tw.ccmos.mobi.playcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by mosluce on 15/10/16.
 */
public class CameraView extends CropTextureView implements TextureView.SurfaceTextureListener {
    private Camera mCamera;
    private Context mContext;
    private boolean isPreviewOn;

    public CameraView(Context context) {
        super(context);

        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            stopPreview();
            mCamera.setPreviewTexture(surface);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void startPreview() {
        if (!isPreviewOn && mCamera != null) {
            isPreviewOn = true;
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (isPreviewOn && mCamera != null) {
            isPreviewOn = false;
            mCamera.stopPreview();
        }
    }
}
