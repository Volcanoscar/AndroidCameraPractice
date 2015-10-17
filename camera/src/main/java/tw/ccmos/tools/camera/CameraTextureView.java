package tw.ccmos.tools.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by mosluce on 15/10/17.
 */
public class CameraTextureView extends CropTextureView implements TextureView.SurfaceTextureListener {

    Camera mCamera;
    Camera.Size mPreviewSize;
    Camera.Size mVideoSize;
    CameraOrientation mCameraOrientation;
    MediaRecorder mMediaRecorder;
    boolean isRecording;
    String mVideoPath;
    Surface mSurface;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);

        //設定預覽視圖
        try {
            mCamera.setPreviewTexture(surface);
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


    public CameraTextureView(Context context) {
        super(context);

        setSurfaceTextureListener(this);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSurfaceTextureListener(this);
    }

    /**
     * 開始預覽
     */
    public void startPreview() {
        //非同步啟動預覽
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCamera.startPreview();
            }
        });
    }

    /**
     * 停止預覽
     */
    public void stopPreview() {
        mCamera.stopPreview();
    }

    /**
     * 開始錄影
     */
    public void startRecord() {
        if (!isRecording) {
            isRecording = true;

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //進行拍攝設定
                    if (prepareVideoRecorder()) {
                        //非同步啟動錄影
                        mMediaRecorder.start();
                    } else {
                        //啟動失敗 -> 釋放
                        releaseMediaRecorder();
                    }
                }
            });
        }
    }

    /**
     * 停止錄影
     */
    public void stopRecord() {
        if (isRecording) {
            isRecording = false;

            mMediaRecorder.stop();
            releaseMediaRecorder();
            mCamera.lock();
        }
    }

    /**
     * 設定相機及初始化相關設定
     *
     * @param camera      相機實體
     * @param orientation 指定相機方向
     */
    public void setCamera(Camera camera, CameraOrientation orientation) {
        mCamera = camera;
        mCameraOrientation = orientation;

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

    public void setVideoPath(String videoPath) {
        this.mVideoPath = videoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        if (mCameraOrientation == CameraOrientation.PORTRAIT && mPreviewSize.width > mPreviewSize.height) {
            mMediaRecorder.setOrientationHint(90);
        }

        if (mCameraOrientation == CameraOrientation.LANDSCAPE && mPreviewSize.width < mPreviewSize.height) {
            mMediaRecorder.setOrientationHint(270);
        }

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = mPreviewSize.width;
        profile.videoFrameHeight = mPreviewSize.height;
        mMediaRecorder.setProfile(profile);

        // Step 4: Set output file
        if (mVideoPath == null)
            mVideoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + String.format("%d.mp4", new Date().getTime());

        mMediaRecorder.setOutputFile(mVideoPath);

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("RECORDER", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("RECORDER", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;
    }

    public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }
}
