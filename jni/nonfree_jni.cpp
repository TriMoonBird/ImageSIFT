// JNI C++ Native code using nonfree module of opencv2.4.9

#include <jni.h>

#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include <opencv2/nonfree/nonfree.hpp>

using namespace cv;

void mat2VectorKeyPoint(Mat& mat, vector<KeyPoint>& v_kp)
{
    v_kp.clear();
    // CHECK_MAT(mat.type()==CV_32FC(7) && mat.cols==1);
    for(int i=0; i<mat.rows; i++)
    {
        Vec<float, 7> v = mat.at< Vec<float, 7> >(i, 0);
        KeyPoint kp(v[0], v[1], v[2], v[3], v[4], (int)v[5], (int)v[6]);
        v_kp.push_back(kp);
    }
    return;
}

void vectorKeyPoint2Mat(vector<KeyPoint>& v_kp, Mat& mat)
{
    int count = (int)v_kp.size();
    mat.create(count, 1, CV_32FC(7));
    for(int i=0; i<count; i++)
    {
        KeyPoint kp = v_kp[i];
        mat.at< Vec<float, 7> >(i, 0) = Vec<float, 7>(kp.pt.x, kp.pt.y, kp.size, kp.angle, kp.response, (float)kp.octave, (float)kp.class_id);
    }
}

vector<KeyPoint> nativeFeatureDetect(Mat& image, Mat& descriptor)
{
	SiftFeatureDetector detector;
	vector<KeyPoint> keypoints;
	detector.detect(image, keypoints);
	detector.compute(image, keypoints, descriptor);
	return keypoints;
}

// JNI interface functions, be careful about the naming.
extern "C" 
{
	JNIEXPORT void JNICALL Java_org_opencv_samples_sift_NonfreeJNILib_performNativeSift(JNIEnv * env, jlong addrImg, jlong addrKeypoints, jlong addrDescriptor);
};

JNIEXPORT void JNICALL Java_org_opencv_samples_sift_NonfreeJNILib_performNativeSift(JNIEnv *env, jlong addrImg, jlong addrKeypoints, jlong addrDescriptor)
{
	Mat& image = *(Mat *)addrImg;
	Mat& keypointsMat = *(Mat *)addrKeypoints;

	//mat2VectorKeyPoint(keypointsMat, keypointsVector);
	Mat& descriptor = *(Mat *)addrDescriptor;

	vector<KeyPoint> keypointsVector = nativeFeatureDetect(image, descriptor);

	vectorKeyPoint2Mat(keypointsVector, keypointsMat);
}
