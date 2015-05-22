#ifndef MVISION_H
#define MVISION_H

#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/imgproc/imgproc.hpp"
#include "StagePoint.h"
#include "Robot.h"

using namespace cv;
using namespace std;

struct calibrationResult 
{
  int ok;
  CvSeq* circles;
};

class MVision
{
	StagePoint topLeft;
	StagePoint topRight;
	StagePoint bottomRight;
	StagePoint bottomLeft;
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
  void visualizeStage(vector<Robot*> robots);
  StagePoint findCircle();
  //void findPointForCorner(StageCorner* corner);
  void setTopLeft(StagePoint p);
  void setTopRight(StagePoint p);
  void setBottomRight(StagePoint p);
  void setBottomLeft(StagePoint p);
  //void setCalibrationPoints(vector<CvPoint> points);
};

#endif
