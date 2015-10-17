package tw.ccmos.tools.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.TextureView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mosluce on 15/10/17.
 */
public class RecorderTextureView extends CropTextureView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    private Camera.Size mPreviewSize;
    private Camera.Size mVideoSize;
    private CameraOrientation mCameraIrientation;
    private MediaRecorder mRecorder;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //自動開始預覽
        try {
            mCamera.setPreviewTexture(surface);
            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
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

    public enum CameraOrientation {
        PORTRAIT,
        LANDSCAPE
    }


    public RecorderTextureView(Context context) {
        super(context);

        setSurfaceTextureListener(this);
    }

    public RecorderTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    public RecorderTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSurfaceTextureListener(this);
    }

    public void startPreview() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCamera.startPreview();
            }
        });
    }

    public void stopPreview() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCamera.stopPreview();
            }
        });
    }

    public void setMeadiaRecorder(MediaRecorder meadiaRecorder) {
        mRecorder = meadiaRecorder;
    }

    public void setCamera(Camera camera, CameraOrientation orientation) {
        mCamera = camera;
        mCameraIrientation = orientation;

        //取出參數
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();

        if (supportedVideoSizes != null) {
            Collections.sort(supportedVideoSizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size sizeA, Camera.Size sizeB) {
                    if (sizeA.width * sizeA.height > sizeB.width * sizeB.height) return -1;
                    else if (sizeA.width * sizeA.height < sizeB.width * sizeB.height) return 1;
                    else return 0;
                }
            });

            mVideoSize = supportedVideoSizes.get(0);
        }

        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();

        if (supportedPreviewSizes != null) {
            Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size sizeA, Camera.Size sizeB) {
                    if (sizeA.width * sizeA.height > sizeB.width * sizeB.height) return -1;
                    else if (sizeA.width * sizeA.height < sizeB.width * sizeB.height) return 1;
                    else return 0;
                }
            });

            mPreviewSize = supportedPreviewSizes.get(0);
        }

        List<String> supportedFocusModes = parameters.getSupportedFocusModes();

        //設定
        //自動對焦
        if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        //預覽尺寸
        if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);


        //套用設定
        mCamera.setParameters(parameters);

        //相機轉向
        if (orientation == CameraOrientation.PORTRAIT && mPreviewSize.width > mPreviewSize.height) {
            mCamera.setDisplayOrientation(90);

            setPreviewSize(mPreviewSize.height, mPreviewSize.width);
        }


        if (orientation == CameraOrientation.LANDSCAPE && mPreviewSize.width < mPreviewSize.height) {
            mCamera.setDisplayOrientation(270);

            setPreviewSize(mPreviewSize.height, mPreviewSize.width);
        }

        requestLayout();
    }
}
