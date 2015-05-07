
#include <cv.h>
#include <highgui.h>
#include <iostream>
//#include <imgproc.h>
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;
using namespace std;

class MVision
{
 public:
  MVision();
  int init(int camera);
  bool showImage();
};
