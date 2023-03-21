package com.hbb.encode;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hbb.common.IEncodeDataListener;
import com.hbb.common.IVideoEncode;
import com.hbb.common.utils.GlobalVal;
import com.hbb.common.utils.StreamFile;
import com.hbb.encode.helper.HbbEncodeHelper;
import com.hbb.encode.widget.CameraTextureView;

public class MainActivity extends BaseActivity implements CameraTextureView.onYuvCallback, IEncodeDataListener {
    private static final String TAG = "MainActivity";

    private CameraTextureView mSfvTest;
    private IVideoEncode softEncode;

    private IVideoEncode hardEncode;


    /**
     * 缺失内容：
     *  - SPS和PPS保存和I帧一起反馈
     *  - 释放
     *  - 硬编码  - 进行中
     *  - 适配各种颜色
     *  - 缩放
     * @return
     */

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {
        mSfvTest = (CameraTextureView) findViewById(R.id.sfv_test);

    }

    @Override
    protected void initData() {

        mSfvTest.setCameraCallback(this);
    }

    @Override
    public void onYUVFrame(byte[] yuvData) {
//        Log.d(TAG, "onYUVFrame: " + yuvData.length);
        if(softEncode != null)
        softEncode.putData(yuvData);
        if(hardEncode != null)
        hardEncode.putData(yuvData);
    }

    @Override
    public void onH264Data(byte[] data, int length, boolean isKeyFrame, boolean isSoftware, String flag) {
        if(isSoftware){
            Log.i(TAG, "postData: " + length + ",isKeyFrameSoftware :" + isKeyFrame);
        }else{
            Log.d(TAG, "postData: " + length + ",isKeyFrameHardware :" + isKeyFrame);
        }

//        StreamFile.writeContent(data);
        StreamFile.writeBytes(data,isSoftware,flag);
    }

    public void startEncode(View view) {


        Toast.makeText(this, "开始!!", Toast.LENGTH_SHORT).show();

        softEncode = HbbEncodeHelper.getInstance().makeVideoEncode(GlobalVal.width, GlobalVal.height,
                GlobalVal.fps, GlobalVal.bitrate, true, GlobalVal.testSoftWareFlag, this);

        hardEncode = HbbEncodeHelper.getInstance().makeVideoEncode(GlobalVal.width, GlobalVal.height,
                GlobalVal.fps, GlobalVal.bitrate, true, GlobalVal.testHardWareFlag, this);

    }

    public void stopEncode(View view) {
        Toast.makeText(this, "停止", Toast.LENGTH_SHORT).show();


        HbbEncodeHelper.getInstance().release();
    }
}




