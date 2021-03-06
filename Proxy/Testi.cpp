#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;
using namespace std;

int threshold_value = 0;
int threshold_type = 3;
int const max_BINARY_value = 255;

int bw_threshold = 180;
int iratio = 4;
int min_dist = 50;
int cd_threshold = 100;
int acc_threshold = 20;
int min_rad = 2;
int max_rad = 30;


struct calibrationResult {
	int ok;
	CvSeq* circles;
};

void testi();
void printDetectionParameters();
calibrationResult testCalibration(CvCapture* cv_cap);
calibrationResult calibrate(CvCapture* cv_cap);
vector<Point2f> detectCorners(IplImage* source);
void correctPerspective(IplImage* source);
CvSeq* findCircles(IplImage* source);
IplImage* processImage(IplImage* source);
void visualizeDetection(IplImage* source, CvSeq* detection);


int main(int argc,char *argv[])
{
	int cam = 0;
	cout << "Args: " << argc << endl;
	if(argc == 2) cam = *argv[1] - '0';
	
	cout << "OpenCV verision: " << CV_VERSION << endl;
	cout << "Using camera '" << cam << "'" << endl;
    
    int c;
    IplImage* color_img;
    CvCapture* cv_cap = cvCaptureFromCAM(cam);
    
    calibrationResult result = calibrate(cv_cap);
    if(result.ok == 0) cout << "Calibration successful!" << endl;
    else cout << "Calibration failed.." << endl;
    
    for(;;) {
        color_img = cvQueryFrame(cv_cap);
        if(color_img != 0) {
            //cvShowImage("Original", color_img);
            
			IplImage* im_bw = processImage(color_img);
            //detectCorners(im_bw);
            
            //CvSeq* circles = findCircles(im_bw);

			//visualizeDetection(color_img, circles);
			correctPerspective(im_bw);
    		
        }
        c = cvWaitKey(500);
        if(c == 27)
            break; // if ESC, break and quit
    }
    
    
    cvReleaseCapture(&cv_cap);
    return 0;
}

void visualizeDetection(IplImage* source, CvSeq* detection) {
	for (int i = 0; i < detection->total; i++) {
    	float *p = (float*)cvGetSeqElem(detection, i);
        CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
        CvScalar val = cvGet2D(source, center.y, center.x);
        //if (val.val[0] < 1) continue;
        cvCircle(source, center, cvRound(p[2]+5), CV_RGB(0,255,0), 1, CV_AA, 0);
    }
    cvShowImage("Detection", source); // show frame
}

void correctPerspective(IplImage* source) {
	Mat src = cvarrToMat(source);
	// Define the destination image
	Mat quad = Mat::zeros(src.rows, src.cols, CV_8UC3);

	vector<Point2f> corners = detectCorners(source);
	
	if(corners.size() == 4) {
	// Corners of the destination image
	std::vector<cv::Point2f> quad_pts;
	quad_pts.push_back(cv::Point2f(0, 0));
	quad_pts.push_back(cv::Point2f(quad.cols, 0));
	quad_pts.push_back(cv::Point2f(quad.cols, quad.rows));
	quad_pts.push_back(cv::Point2f(0, quad.rows));

	// Get transformation matrix
	Mat transmtx = getPerspectiveTransform(corners, quad_pts);
	
	//const IplImage* tst(source);
	//Mat(const IplImage* img, bool copyData=false);
	//Mat src(tst, true);
	
	// Apply perspective transformation
	warpPerspective(src, quad, transmtx, quad.size());
	IplImage iplImg = quad;
	cvShowImage("quadrilateral", &iplImg);
	} else {
		cout << "Corner detection failed.." << endl;
	}
}

bool wayToSort(Point2f i, Point2f j) { return i.y < j.y; }

vector<Point2f> detectCorners(IplImage* source) {
	CvSeq* circles = findCircles(source);
	
	vector<Point2f> corners;
	
	if(circles->total == 4) {
		vector<Point2f> tmp;
		for(int i = 0; i < circles->total; i++) {
			float *p = (float*)cvGetSeqElem(circles, i);
        	CvPoint point = cvPoint(cvRound(p[0]),cvRound(p[1]));
        	tmp.push_back(Point2f(point.x, point.y));
		}
		sort(tmp.begin(), tmp.end(), wayToSort);
		
		Point2f tl = tmp[0].x > tmp[1].x ? tmp[1] : tmp[0];
    	Point2f tr = tmp[0].x > tmp[1].x ? tmp[0] : tmp[1];
    	Point2f bl = tmp[2].x > tmp[3].x ? tmp[3] : tmp[2];
    	Point2f br = tmp[2].x > tmp[3].x ? tmp[2] : tmp[3];
		
		/*
		cout << tl.x << ", " << tl.y << endl;
		cout << tr.x << ", " << tr.y << endl;
		cout << bl.x << ", " << bl.y << endl;
		cout << br.x << ", " << br.y << endl;
		*/
		
		corners.push_back(tl);
		corners.push_back(tr);
		corners.push_back(br);
		corners.push_back(bl);
	}
	return corners;
}

IplImage* processImage(IplImage* source) {
	IplImage *im_gray = cvCreateImage(cvGetSize(source),IPL_DEPTH_8U,1);
    cvCvtColor(source, im_gray, CV_RGB2GRAY);
    
    //cvSmooth( im_gray, im_gray, CV_GAUSSIAN, 3, 3, 0, 0 );
    //cvShowImage("Gray", im_gray);
            
    IplImage* im_bw = cvCreateImage(cvGetSize(im_gray),IPL_DEPTH_8U,1);
    cvThreshold(im_gray, im_bw, bw_threshold, 255, 3);
            
    //int morph_size = 2;
    //Mat element = getStructuringElement( MORPH_RECT, Size( 2*morph_size + 1, 2*morph_size+1 ), Point( morph_size, morph_size ) );
	//morphologyEx( im_bw, im_bw, MORPH_OPEN, element );
	
	cvDilate(im_bw, im_bw, NULL, 6);
	cvErode(im_bw, im_bw, NULL, 2);
	
			
	//correctPerspective(im_bw);
			
	//cvSmooth( im_bw, im_bw, CV_GAUSSIAN, 3, 3, 0, 0 );
	cvShowImage("BW", im_bw);
	return im_bw;
}

calibrationResult calibrate(CvCapture* cv_cap) {
	cout << "Starting calibration" << endl;
	int ok = 0;
	int tries = 0;
	calibrationResult result;
	do {
		result = testCalibration(cv_cap);
		switch(result.ok) 
		{
			case 0:
				cout << "Detected 4 points, exiting calibration.." << endl;
				ok = 1;
				break;
			case 1:
				cout << "Too many points detected, changing parameters.." << endl;
				if(cd_threshold < 300) {
					cd_threshold += 10;
				} else if(acc_threshold < 40) {
					acc_threshold++;
				} else if(max_rad > 10) {
					max_rad--;
				} else {
					ok = -1;
				}
				break;
			case -1:
				cout << "Too few points detected, changing parameters.." << endl;
				if(cd_threshold > 100) {
					cd_threshold -= 10;
				} else if(acc_threshold > 10) {
					acc_threshold--;
				} else if(max_rad < 50) {
					max_rad++;
				} else {
					ok = -1;
				}
				break;
		}
		tries++;
		printDetectionParameters();
		cvWaitKey(100);
	} while(ok == 0);
	
	cout << "Calibration ended, status: " << ok << endl;
	cout << "Found corners:" << endl;
	for(int i = 0; i < result.circles->total; i++) {
		float *p = (float*)cvGetSeqElem(result.circles, i);
        CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
        cout << i << ": " << center.x << ", " << center.y << endl;
	}
	
	return result;
}

void printDetectionParameters() {
	cout << "iratio:        " << iratio << endl;
	cout << "min_dist:      " << min_dist << endl;
	cout << "cd_threshold:  " << cd_threshold << endl;
	cout << "acc_threshold: " << acc_threshold << endl;
	cout << "min_rad:       " << min_rad << endl;
	cout << "max_rad:       " << max_rad << endl;
}

calibrationResult testCalibration(CvCapture* cv_cap) {
	calibrationResult result;
	result.ok = 0;
	for(int i = 0; i < 30; i++) {
		IplImage* img = cvQueryFrame(cv_cap);
		IplImage* processed = processImage(img);
		result.circles = findCircles(processed);
		visualizeDetection(img, result.circles);
		if(result.circles->total > 4) {
			result.ok = 1;
			break;
		} else if(result.circles->total < 4) {
			result.ok = -1;
			break;
		}
		cvWaitKey(100);
	}
	
	return result;
}

CvSeq* findCircles(IplImage* source) {
	CvMemStorage* storage = cvCreateMemStorage(0);
    //cvThreshold( color_img, dst, threshold_value, max_BINARY_value,threshold_type );
    CvSeq* circles = cvHoughCircles(source, storage, CV_HOUGH_GRADIENT,
    	iratio,      // inverse ratio of the accumulator resolution
    	min_dist,      // minimum distance between circle centres
    	cd_threshold,    // higher threshold value for Canny
     	acc_threshold,     // accumulator threshold for the circle centers; smaller->more false circles
     	min_rad,  // minimum radius
    	max_rad);   // maximum radius
    cout << "Found circles: " << circles->total << endl;
    return circles;
}

void testi() {
	cout << "testi" << endl;
}

