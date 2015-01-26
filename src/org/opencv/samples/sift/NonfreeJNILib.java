package org.opencv.samples.sift;

import android.util.Log;

public class NonfreeJNILib {
	private static String TAG = "SiftNonfree";
	static {
		try {
			// Load necessary libraries.
			System.loadLibrary("opencv_java");
			System.loadLibrary("nonfree");
			System.loadLibrary("nonfree_jni");
		} catch( UnsatisfiedLinkError e ) {
			Log.e(TAG, "Native code library failed to load.\n" + e);
		}
    }
    public static native void performNativeSift(long addrImage, long addrKeypoints, long addrDescriptor);
}
