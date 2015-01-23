package org.opencv.samples.sift;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class ImageManipulation {
	
	public static Mat downSamplingImage(Mat src, int factor)
	{
		// Convert Mat to 1-d array
		byte buff[] = new byte[(int) src.total()*src.channels()];
		src.get(0, 0, buff);
		
		// leaving one pixel every factor rows and every factor columns
		byte ans[] = new byte[(int) (src.rows()/factor)*(src.cols()/factor)];
		for (int i = 0; i < src.rows()/factor; ++i) {
			for (int j = 0; j < src.cols()/factor; ++j) {
				ans[i*(src.cols()/factor)+j] = buff[factor*(i*src.cols()+j)];
			}
		}
		
		// Convert 1-d array to Mat
		Mat dst = new Mat(src.rows()/factor, src.cols()/factor, CvType.CV_8U);
		dst.put(0, 0, ans);
		return dst;
	}
}

/*

Mat rotateImage(Mat& src, double angle)
{
	Mat ret;
	Point2f center(src.cols/2.0, src.rows/2.0);
	Mat r = getRotationMatrix2D(center, angle, 1.0);
	warpAffine(src, ret, r, Size(src.cols, src.rows));
	return ret;
}

Mat matMultiply(Mat& A, Mat& B)
{
	CV_Assert(A.channels() == B.channels());
	CV_Assert(A.channels() == 1);
	CV_Assert(A.cols == B.rows);

	Mat C(A.rows, B.cols, CV_64FC1);
	for (int i = 0; i < A.rows; ++i) {
		for (int j = 0; j < A.cols; ++j) {
			for (int k = 0; k < B.cols; ++k) {
				C.at<double>(i,j) += A.at<double>(i,k) * B.at<double>(k,j);
			}
		}
	}
	return C;
}
*/