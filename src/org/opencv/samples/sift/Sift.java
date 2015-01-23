package org.opencv.samples.sift;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
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
	
	public Sift() {
		mKeypointsOne = new MatOfKeyPoint();
		mKeypointsTwo = new MatOfKeyPoint();
		mDescriptorOne = new Mat();
		mDescriptorTwo = new Mat();
		mMatches = new MatOfDMatch();
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
	
	public void preprocessImage() {
		Log.i(TAG, "called preprocessImage");
		mImgOne = ImageManipulation.downSamplingImage(mRawImgOne, DOWNSAMPLE_FACTOR);
		mImgTwo = ImageManipulation.downSamplingImage(mRawImgTwo, DOWNSAMPLE_FACTOR);
	}
	
	public void saveImage(String path) {
		Log.i(TAG, "called saveImage");
		Highgui.imwrite(path, mImgOne);
		Highgui.imwrite(path+"_2.jpg", mImgTwo);
	}

	public void detectImage() {
		Log.i(TAG, "called detectImage");
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		Log.i(TAG, "dectect 1");
		detector.detect(mImgOne, mKeypointsOne);
		Log.i(TAG, "dectect 2");
		detector.detect(mImgOne, mKeypointsTwo);
		Log.i(TAG, "dectect 3");
	}
	
	public void describeImage() {
		Log.i(TAG, "called describeImage");
		DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		Log.i(TAG, "describe 1");
		descriptor.compute(mRawImgOne, mKeypointsOne, mDescriptorOne);
		Log.i(TAG, "describe 2");
		descriptor.compute(mRawImgTwo, mKeypointsTwo, mDescriptorTwo);
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


/*
int main( int argc, char** argv )
{
  String path_1 = "../Pic/img_1.jpg";
  String path_2 = "../Pic/img_2.jpg";

  Mat rawimg_1 = imread( path_1, CV_LOAD_IMAGE_COLOR );
  Mat rawimg_2 = imread( path_2, CV_LOAD_IMAGE_COLOR );

  if( !rawimg_1.data || !rawimg_2.data )
  { std::cout<< " --(!) Error reading images " << std::endl; return -1; }


  Mat img_1 = downSamplingImage(rawimg_1, DOWNSAMPLE_FACTOR);
  Mat img_2 = downSamplingImage(rawimg_2, DOWNSAMPLE_FACTOR);

  //-- Step 1: Detect the keypoints using SIFT Detector
  int minHessian = MIN_HESSIAN;

  SiftFeatureDetector detector( minHessian );

  std::vector<KeyPoint> keypoints_1, keypoints_2;

  clock_t detectStart = clock();
  detector.detect( img_1, keypoints_1 );
  detector.detect( img_2, keypoints_2 );
  clock_t detectEnd = clock();
  printTimeMs("Detect time: ", clockDiffMs(detectEnd, detectStart));

  //-- Step 2: Calculate descriptors (feature vectors)
  SiftDescriptorExtractor extractor;

  Mat descriptors_1, descriptors_2;

  clock_t extractStart = clock();
  extractor.compute( img_1, keypoints_1, descriptors_1 );
  extractor.compute( img_2, keypoints_2, descriptors_2 );
  clock_t extractEnd = clock();
  printTimeMs("Extract time: ", clockDiffMs(extractEnd, extractStart));

  //-- Step 3: Matching descriptor vectors using FLANN matcher
  FlannBasedMatcher matcher;
  std::vector< DMatch > matches;

  clock_t matchStart = clock();
  matcher.match( descriptors_1, descriptors_2, matches );
  clock_t matchEnd = clock();
  printTimeMs("Match time: ", clockDiffMs(matchEnd, matchStart));

  double max_dist = 0; double min_dist = 100;

  //-- Quick calculation of max and min distances between keypoints
  for( int i = 0; i < descriptors_1.rows; i++ )
  {
	double dist = matches[i].distance;
    if( dist < min_dist ) min_dist = dist;
    if( dist > max_dist ) max_dist = dist;
  }

  cout << "-- Max dist : " << max_dist << endl;
  cout << "-- Min dist : " << min_dist << endl;

  //-- Draw only "good" matches (i.e. whose distance is less than TIME_THRES*min_dist,
  //-- or a small arbitary value ( 0.02 ) in the event that min_dist is very
  //-- small)
  std::vector< DMatch > good_matches;

  for( int i = 0; i < descriptors_1.rows; i++ )
  {
	if( matches[i].distance <= max(TIME_THRES*min_dist, 0.02) )
    { good_matches.push_back( matches[i]); }
  }

  //-- Draw only "good" matches
  Mat img_matches;
  drawMatches( img_1, keypoints_1, img_2, keypoints_2,
               good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
               vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );

  //-- Show detected matches
  //imshow( "Good Matches", img_matches );
  imwrite("../Result/matchPicture.jpg", img_matches);

  //for( int i = 0; i < (int)good_matches.size(); i++ )
  //{ printf( "-- Good Match [%d] Keypoint 1: %d  -- Keypoint 2: %d  \n", i, good_matches[i].queryIdx, good_matches[i].trainIdx ); }

  ofstream matchFile ("../Result/matchPoints.txt");
  if (!matchFile.is_open())
  { cout << "File Error" << endl; return -2; }

  for( int i = 0; i < (int)good_matches.size(); i++ )
  {
	int j = good_matches[i].queryIdx;
	int k = good_matches[i].trainIdx;
	matchFile << (int)keypoints_1[j].pt.x << " " << (int)keypoints_1[j].pt.y << " "
			<< (int)keypoints_2[k].pt.x << " " << (int)keypoints_2[k].pt.y << endl;
  }
  matchFile.close();

  waitKey(0);

  return 0;
}
*/
