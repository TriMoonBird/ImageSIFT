package org.opencv.samples.sift;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;


public class SiftActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG                 = "SiftActivity";
    
    public static final int		 WAITING_TIME		 = 0; //in ms

    private boolean				 mCameraReady;
    private Sift				 mSift;
    private CameraBridgeViewBase mOpenCvCameraView;

    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
        	    	/*
        	    	File sdcard = Environment.getExternalStorageDirectory();
        	        String path = sdcard + "/Exp/";
        	        
        	        mSift = new Sift();
        	        mSift.setPathOne(path+"img_1.jpg");
        	        mSift.setPathTwo(path+"img_2.jpg");
        	        mSift.readImage();
        	        
        	        mSift.preprocessImage();
        	        mSift.nativeSiftImage();
        	        //mSift.detectImage();
        	        //mSift.describeImage();
        	        mSift.matchImage();
        	        mSift.formMatchPoints(50);
        	        mSift.drawGoodMatches();
        	        mSift.postprocessImage();
        	        mSift.saveImage(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
        	        mSift.writeMatches(path, String.valueOf(System.currentTimeMillis()) + ".txt");
        	        */
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public SiftActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    // Called when the activity is first created.
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.sift_activity_surface_view);
        
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.sift_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
    	Log.i(TAG, "called onPause");
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
    	Log.i(TAG, "called onResume");
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

    public void onDestroy() {
    	Log.i(TAG, "called onDestroy");
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    	mCameraReady = false;
    	mSift = new Sift();
    }

    public void onCameraViewStopped() {
    	mCameraReady = false;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
    	Mat gray = inputFrame.gray();
    	
    	try {
			Thread.sleep(WAITING_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	if (mCameraReady == true) {
	    	mSift.setRawImgOne(mSift.getRawImgTwo());
	    	mSift.setRawImgTwo(gray);
	    	
	    	File sdcard = Environment.getExternalStorageDirectory();
	        String path = sdcard + "/Exp/";
	        
	        mSift.preprocessImage();
	        mSift.nativeSiftImage();
	        //mSift.detectImage();
	        //mSift.describeImage();
	        mSift.matchImage();
	        mSift.formMatchPoints(50);
	        mSift.drawGoodMatches();
	        mSift.postprocessImage();
	        mSift.saveImage(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
	        mSift.writeMatches(path, String.valueOf(System.currentTimeMillis()) + ".txt");
    	} else {
    		mCameraReady = true;
    		mSift.setRawImgTwo(gray);
    	}
    	
    	return gray;

    }
    
}
