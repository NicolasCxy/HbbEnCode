package com.hbb.encode.widget;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;


import com.hbb.common.utils.GlobalVal;

import java.io.IOException;

/**
 * Created by able on 2019/2/25.
 */

public class CameraTextureView extends TextureView implements Camera.PreviewCallback{

    Context mContext;
    Camera mCamera;//相机类
    onYuvCallback mCb;//数据回调接口
    private Camera.Parameters parameters;
    private int currCameraType = -1;
    private SurfaceTexture surfaceTexture;

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setSurfaceTextureListener(surfaceTextureListener);

    }

   public interface onYuvCallback {
       void onYUVFrame(byte[] dataStream);
    }

    public void setCameraCallback(onYuvCallback mCb) {
        this.mCb = mCb;
    }

    //寻找相机
    private int findCamera(boolean isfront) {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (isfront) {
                // CAMERA_FACING_FRONT前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return camIdx;
                }
            } else {
                // CAMERA_FACING_BACK后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return camIdx;
                }
            }
        }
        return -1;
    }

    //打开相机
    private Camera getCamera(int type) {
            Camera camera = null;
            int cameraId = findCamera(type == 1 ? false : true);
            try {
                camera = Camera.open(cameraId);
        } catch (Exception e) {
            camera = null;
        }
            return camera;
    }


    int count = 0;
    byte[] nv12;
    private boolean isStart = false;
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
//        if(count % 2 == 0){
//            if(mCb!= null && isStart){
//                Log.i(TAG, "onPreviewFrame: " + bytes.length);
//                mCb.onYUVFrame(bytes);
//            }
//        }
//
//        count++;


//        if(mCb!= null){
//            mCb.onPreView(currCameraType,bytes);
//        }

//        if (count < 20) {
////            if(nv12 == null){
////                nv12 = new byte[bytes.length];
////            }
//            nv12 = YuvUtils.nv21toNV12(bytes);
//            SaveFileUtils.getInstance().saveData(nv12);
//        }
//        count++;

        if(mCb!= null){
            mCb.onYUVFrame(bytes);
        }

//        if(null != ableEncode){
//            ableEncode.addDataToQueue(bytes);
//        }
    }

    SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "onSurfaceTextureAvailable!!");
            getShowCamera(0);
            surfaceTexture = surface;
            setStartPreview(mCamera,surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "onSurfaceTextureSizeChanged!!");

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.i(TAG, "onSurfaceTextureDestroyed!!");
            releaseCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public void getShowCamera(int cameraType){
        currCameraType = cameraType;
        mCamera = getCamera(cameraType);
    }

    private void setStartPreview(Camera camera, SurfaceTexture surface) {
        try {
            if(parameters == null){
                parameters = camera.getParameters();
            }
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewSize(GlobalVal.width, GlobalVal.height);
//            List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();
//            Log.i(TAG, "setStartPreview: " + supportedVideoSizes.size());
//            for(Camera.Size cameraInfo: supportedVideoSizes){
//                Log.i(TAG, "setStartPreview width:" + cameraInfo.width   + "__height:" + cameraInfo.height);
//            }
            camera.setParameters(parameters);
            camera.setPreviewTexture(surface);
            camera.setPreviewCallback(this);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
        }
    }

    //释放Camera
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉摄像头的预览
            mCamera.release();
            mCamera = null;
        }
    }

    private static final String TAG = "CameraSurfaceViews";
    public void reStartCamera(){
        if(null != mCamera){
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        }
    }

    public Camera getCurrentCamera(){
        return mCamera;
    }


    /**
     * 自动对焦的回调方法，用来处理对焦成功/不成功后的事件
     */
    private Camera.AutoFocusCallback mAutoFocus =  new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //TODO:空实现
        }
    };
}
