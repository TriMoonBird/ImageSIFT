package org.opencv.samples.sift;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import android.util.Log;

public class Sift {
	private static final String TAG					= "Sift";
	
	public static final int		DIM_EACH_MATCH		= 4;
	public static final int		TIME_THRES			= 5;
	public static final int		DOWNSAMPLE_FACTOR	= 3;
	
	private String				mPathOne;
	private String				mPathTwo;
	private Mat					mRawImgOne;
	private Mat					mRawImgTwo;
	private Mat					mImgOne;
	private Mat					mImgTwo;
	private MatOfKeyPoint		mKeypointsOne;
	private MatOfKeyPoint		mKeypointsTwo;
	private Mat					mDescriptorOne;
	private Mat					mDescriptorTwo;
	private MatOfDMatch			mMatches;
	private DMatch[]			mMatchArray;
	private int[][]				mMatchPoints;
	private Mat					mMatchImage;
	
	public Sift() {
		mKeypointsOne = new MatOfKeyPoint();
		mKeypointsTwo = new MatOfKeyPoint();
		mDescriptorOne = new Mat();
		mDescriptorTwo = new Mat();
		mMatches = new MatOfDMatch();
		mMatchImage = new Mat();
	}
	
	public void setPathOne(String path) {
		mPathOne = path;
	}
	
	public void setPathTwo(String path) {
		mPathTwo = path;
	}
	
	public boolean readImage() {
		Log.i(TAG, "called readImage");
		mRawImgOne = Highgui.imread(mPathOne, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		mRawImgTwo = Highgui.imread(mPathTwo, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		if (mRawImgOne == null || mRawImgTwo == null) {
			Log.e(TAG, "image reading error");
			return false;
		}
		return true;
	}
	
	public void setRawImgOne(Mat img) {
		mRawImgOne = img;
	}
	
	public void setRawImgTwo(Mat img) {
		mRawImgTwo = img;
	}
	
	public Mat getRawImgOne() {
		return mRawImgOne;
	}
	
	public Mat getRawImgTwo() {
		return mRawImgTwo;
	}
	
	public void preprocessImage() {
		Log.i(TAG, "called preprocessImage");
		mImgOne = ImageManipulation.downSamplingImage(mRawImgOne, DOWNSAMPLE_FACTOR);
		mImgTwo = ImageManipulation.downSamplingImage(mRawImgTwo, DOWNSAMPLE_FACTOR);
	}
	
	public void saveImage(String path, String filename) {
		Log.i(TAG, "called saveImage");
		Highgui.imwrite(path+filename, mMatchImage);
	}

	public void detectImage() {
		Log.i(TAG, "called detectImage");
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		Log.i(TAG, "dectect 1");
		detector.detect(mImgOne, mKeypointsOne);
		Log.i(TAG, "dectect 2");
		detector.detect(mImgTwo, mKeypointsTwo);
		Log.i(TAG, "dectect 3");
	}
	
	public void describeImage() {
		Log.i(TAG, "called describeImage");
		DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		Log.i(TAG, "describe 1");
		descriptor.compute(mImgOne, mKeypointsOne, mDescriptorOne);
		Log.i(TAG, "describe 2");
		descriptor.compute(mImgTwo, mKeypointsTwo, mDescriptorTwo);
		Log.i(TAG, "describe 3");
	}
	
	public void matchImage() {
		Log.i(TAG, "called matchImage");
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		matcher.match(mDescriptorTwo, mDescriptorOne, mMatches);
		Log.i(TAG, "MATCHES: " + mMatches.toString());
	}
	
	public void formMatchPoints(int num) {
		Log.i(TAG, "called formMatchPoints");
		mMatchArray = mMatches.toArray();
		int pairNumber = Math.min(num, mMatchArray.length);
		// sort the matches by distance
		Arrays.sort(mMatchArray, new Comparator<DMatch>() {
			public int compare(DMatch one, DMatch two) {
				return Float.compare(one.distance, two.distance);
			}
		});
		// get the first pairNumber match points
		mMatchPoints = new int[pairNumber][DIM_EACH_MATCH];
		KeyPoint[] keyPointsOne = mKeypointsOne.toArray();
		KeyPoint[] keyPointsTwo = mKeypointsTwo.toArray();
		for (int i = 0; i < pairNumber; ++i) {
			mMatchPoints[i][0] = (int)keyPointsOne[mMatchArray[i].trainIdx].pt.x;
			mMatchPoints[i][1] = (int)keyPointsOne[mMatchArray[i].trainIdx].pt.y;
			mMatchPoints[i][2] = (int)keyPointsTwo[mMatchArray[i].queryIdx].pt.x;
			mMatchPoints[i][3] = (int)keyPointsTwo[mMatchArray[i].queryIdx].pt.y;
		}
	}
	
	public void postprocessImage() {
		Log.i(TAG, "called postprocessImage");
        for (int i = 0; i < mMatchPoints.length; ++i) {
        	for (int j = 0; j < DIM_EACH_MATCH; ++j) {
        		mMatchPoints[i][j] *= DOWNSAMPLE_FACTOR;
        	}
        }
	}
	
	public int[][] getMatchPoints() {
		Log.i(TAG, "called getMatchPoints");
		if (mMatchPoints == null) {
			Log.e(TAG, "null matched points");
		}
		return mMatchPoints;
	}
	
	public void drawGoodMatches() {
		Log.i(TAG, "called drawGoodMatches");
        Features2d.drawMatches(mImgTwo, mKeypointsTwo, mImgOne, mKeypointsOne, 
                mMatches, mMatchImage, Scalar.all(-1), Scalar.all(-1), 
                new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);
	}
	
	public void writeMatches(String path, String filename) {
		Log.i(TAG, "called writeMatches");
		try {
            FileWriter out = new FileWriter(new File(path, filename));
            for (int i = 0; i < mMatchPoints.length; ++i) {
            	for (int j = 0; j < DIM_EACH_MATCH; ++j) {
            		out.write(Integer.toString(mMatchPoints[i][j]) + ' ');
            	}
            	out.write(System.getProperty("line.separator"));
            }
            out.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
	}
	
}

