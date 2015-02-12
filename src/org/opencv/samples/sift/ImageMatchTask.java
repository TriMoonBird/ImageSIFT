package org.opencv.samples.sift;

import java.io.File;

import org.opencv.core.Mat;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

class ImageMatchTask extends AsyncTask<Mat, Void, int[][]> {
	private static String	TAG = "SiftAsnycTask";
	
	public static final int MATCHED_POINTS_NUMBER = 50;
	
	private Sift			mSift;

	protected void onPreExecute() {
		Log.i(TAG, "called onPreExecute");
		mSift = new Sift();
	}
	
	@Override
	protected int[][] doInBackground(Mat... params) {
		Log.i(TAG, "called doInBackground");

    	File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard + "/Exp/";
        mSift.setRawImgOne(params[0]);
        mSift.setRawImgTwo(params[1]);
        mSift.preprocessImage();
        //mSift.nativeSiftImage();
        mSift.detectImage();
        mSift.describeImage();
        mSift.matchImage();
        mSift.formMatchPoints(MATCHED_POINTS_NUMBER);
        mSift.drawGoodMatches();
        mSift.postprocessImage();
        mSift.saveImage(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
        mSift.writeMatches(path, String.valueOf(System.currentTimeMillis()) + ".txt");
        return mSift.getMatchPoints();
	}
	
}
