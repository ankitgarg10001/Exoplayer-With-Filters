package com.ankit.videofilter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.util.Log;

public class FilterView {
	private static final String TAG = "VideoFilter::MainActivity";

	private Mat mRgba15;

	public Mat filterFrame(Mat inputFrame) {
		return mRgba15;
	}

	private void drawfilter() {

		Core.rectangle(mRgba15, new Point(0, 0), new Point(mRgba15.rows(),
				mRgba15.cols()), new Scalar(0, 0, 255, 100), -1);
	}

	public void preparefilter(int width, int height) {
		Log.d(TAG, "Preparing Filter");
		mRgba15 = new Mat(height, width, CvType.CV_8UC4);
		Log.d(TAG, mRgba15.rows()+":"+mRgba15.cols());
		drawfilter();
		Log.d(TAG, "filter ready");

	}
}
