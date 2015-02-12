package org.opencv.samples.sift;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


public class SiftActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG                 = "SiftActivity";
    
    public static final int		 WAITING_TIME		 = 200; //in ms

    private boolean				 mCameraReady;
    //private Sift				 mSift;
    private CameraBridgeViewBase mOpenCvCameraView;
    private long				 mLastPicTime;
    private Mat					 mImageOne;
    private Mat					 mImageTwo;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
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
    public void onPause() {
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
    	mLastPicTime = System.currentTimeMillis();
    	mImageOne = new Mat();
    	mImageTwo = new Mat();
    }

    public void onCameraViewStopped() {
    	mCameraReady = false;
    }
    
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	Mat gray = inputFrame.gray();
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - mLastPicTime >= WAITING_TIME) {
    		mLastPicTime = currentTime;
	    	if (mCameraReady == true) {
	    		mImageTwo.copyTo(mImageOne);
	    		gray.copyTo(mImageTwo);
	    		ImageMatchTask task = new ImageMatchTask();
    			task.execute(mImageOne, mImageTwo);
	    	} else {
	    		mCameraReady = true;
	    		gray.copyTo(mImageTwo);
	    	}
    	}
    	return gray;
    }
    
}
