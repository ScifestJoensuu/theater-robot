#ifndef MVISION_H
#define MVISION_H

#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;
using namespace std;

struct calibrationResult 
{
  int ok;
  CvSeq* circles;
};

class MVision
{
	CvPoint topLeft;
	CvPoint topRight;
	CvPoint bottomRight;
	CvPoint bottomLeft;
	vector<Point2f> detectCorners(IplImage* source);
	IplImage* correctPerspective(IplImage* source, IplImage* target);

void testi();
void printDetectionParameters();
calibrationResult testCalibration(CvCapture* cv_cap, int target_points);
calibrationResult calibrate();
calibrationResult calibrate(int target);
//vector<Point2f> detectCorners(IplImage* source);

CvSeq* findCircles(IplImage* source);
CvSeq* findCircles(int target_count);
IplImage* processImage(IplImage* source);
IplImage* getImage();
IplImage* getSubImage();
IplImage* getProcessedSubImage();
void visualizeDetection(IplImage* source, CvSeq* detection, string msg);
//CvPoint findCircle();
//void findPointForCorner(StageCorner* corner);

 public:
  MVision();
  int init(int camera);
  bool showImage();
  bool showSubImage();
  CvPoint findCircle();
  //void findPointForCorner(StageCorner* corner);
  void setTopLeft(CvPoint p);
  void setTopRight(CvPoint p);
  void setBottomRight(CvPoint p);
  void setBottomLeft(CvPoint p);
  //void setCalibrationPoints(vector<CvPoint> points);
};

#endif
