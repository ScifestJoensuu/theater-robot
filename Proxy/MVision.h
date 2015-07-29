#ifndef MVISION_H
#define MVISION_H

#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/opencv.hpp"
#include <vector>
#include "StagePoint.h"
#include "Robot.h"
#include "sys/sysinfo.h"

using namespace std;
using namespace cv;

struct calibrationResult 
{
  int ok;
  CvSeq* circles;
};

class MVision
{
IplImage* currentCorrected;
 int stageWidth;
 int stageHeight;
  bool calibrated;
  vector<Robot*> robots;
  StagePoint topLeft;
  StagePoint topRight;
  StagePoint bottomRight;
  StagePoint bottomLeft;
  vector<Point2f> detectCorners();
  IplImage* correctPerspective(IplImage* source, IplImage* target);
  
  void testi(IplImage* a, IplImage* b);
  //  void testi();
  void printDetectionParameters();
  calibrationResult testCalibration(CvCapture* cv_cap, int target_points);
  calibrationResult calibrate(int target);
  //vector<Point2f> detectCorners(IplImage* source);
  
  void findCircles(IplImage* source, CvSeq** seq, CvMemStorage* s);
  CvSeq* findCircles(IplImage* source, CvMemStorage* s);
  CvSeq* findCircles(int target_count);
  CvSeq* findCircles(int target_count, int try_count);
  CvSeq* findCircles(IplImage* source, int target_count, int try_count);
  IplImage* processImage(IplImage** source, IplImage** target);
  IplImage* getImage();
  IplImage* getStageImage(IplImage* cor);
  IplImage* getProcessedStageImage();
  void visualizeDetection(IplImage* source, CvSeq* detection, string msg);
  void drawStage(IplImage* source);
 //CvPoint findCircle();
 //void findPointForCorner(StageCorner* corner);

 public:
  MVision();
  bool stageCalibrated;
  int init(int camera);
  bool showImage();
  bool showStageImage();
  bool dismisImage();
  void visualizeStage(vector<Robot*> robots);
  void visualizeStage(IplImage* source, CvSeq* detection, string msg);
  StagePoint findCircle();
  StagePoint findCircleFromStage();
  StagePoint findCircleFromStage(int tries);
  vector<StagePoint> findCirclesFromStage();
  //void findPointForCorner(StageCorner* corner);
  void setTopLeft(StagePoint p);
  void setTopRight(StagePoint p);
  void setBottomRight(StagePoint p);
  void setBottomLeft(StagePoint p);
  void setStageWidth(int w);
  void setStageHeight(int h);
calibrationResult calibrate();
 void setRobots(vector<Robot*> r);
  //void setCalibrationPoints(vector<CvPoint> points);
};

#endif
