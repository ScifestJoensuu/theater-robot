#ifndef MVISION_H
#define MVISION_H

#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;
using namespace std;

class MVision
{
	CvPoint topLeft;
	CvPoint topRight;
	CvPoint bottomRight;
	CvPoint bottomLeft;
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
  void setBottoLeft(CvPoint p);
  //void setCalibrationPoints(vector<CvPoint> points);
};

#endif
