
#include "MVision.h"

int threshold_value = 0;
int threshold_type = 3;
int const max_BINARY_value = 255;

int bw_threshold = 240;
int iratio = 3;
int min_dist = 10;
int cd_threshold = 150;
int acc_threshold = 20;
int min_rad = 2;
int max_rad = 20;

CvCapture* cv_cap;


MVision::MVision() 
{
}

int MVision::init(int camera) 
{
  cout << ">> Initializing MVision-module" << endl;	
  cout << "OpenCV verision: " << CV_VERSION << endl;
  cout << "Using camera '" << camera << "'" << endl;
    
  int c;
  IplImage* color_img;
  cv_cap = cvCaptureFromCAM(camera);
    
  //calibrationResult result = calibrate(4);
  //calibrationResult result =  calibrate();
  //if(result.ok == 0) cout << "Calibration successful!" << endl;
  //else cout << "Calibration failed.." << endl;
  
  /*    
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
  */
  

  //cvReleaseCapture(&cv_cap);
  return 0;
}

void MVision::setTopLeft(CvPoint p)
{
	topLeft = p;
}

void MVision::setTopRight(CvPoint p)
{
	topRight = p;
}

void MVision::setBottomRight(CvPoint p)
{
	bottomRight = p;
}

void MVision::setBottomLeft(CvPoint p)
{
	bottomLeft = p;
}

bool MVision::showImage()
{
  IplImage* tmp = cvQueryFrame(cv_cap);
  IplImage* bw = processImage(tmp);
  CvSeq* circles = findCircles(bw);
  visualizeDetection(tmp, circles, "Detection");
  cvWaitKey(10);
  return true;
}

bool MVision::showSubImage()
{
  IplImage* tmp = getSubImage();
  IplImage* bw = getProcessedSubImage();
  CvSeq* circles = findCircles(bw);
  visualizeDetection(tmp, circles, "SubImage");
  cvWaitKey(10);
  return true;
}

void MVision::visualizeDetection(IplImage* source, CvSeq* detection, string title)
{
  for (int i = 0; i < detection->total; i++) {
    float *p = (float*)cvGetSeqElem(detection, i);
    CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    CvScalar val = cvGet2D(source, center.y, center.x);
    //if (val.val[0] < 1) continue;
    cvCircle(source, center, cvRound(p[2]+5), CV_RGB(0,255,0), 1, CV_AA, 0);
  }
  try 
    {
      cvShowImage("visual", source); // show frame
    }
  catch (...)
    {
      cout << "Could not show image '" << title << "', exception: " << endl;
    }
}


IplImage* MVision::correctPerspective(IplImage* source, IplImage* target)
{
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
    target = &iplImg;
    return target;
  } else {
    cout << "Corner detection failed.." << endl;
  }
  return NULL;
}

bool wayToSort(Point2f i, Point2f j) 
{ 
  return i.y < j.y; 
}

vector<Point2f> MVision::detectCorners(IplImage* source) 
{
	vector<Point2f> corners;
	corners.push_back(Point2f(topLeft.x, topLeft.y));
	corners.push_back(Point2f(topRight.x, topRight.y));
	corners.push_back(Point2f(bottomRight.x, bottomRight.y));
	corners.push_back(Point2f(bottomLeft.x, bottomLeft.y));
	/*
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

		
    corners.push_back(tl);
    corners.push_back(tr);
    corners.push_back(br);
    corners.push_back(bl);
  }
*/
  return corners;
}

IplImage* MVision::processImage(IplImage* source) 
{
  IplImage *im_gray = cvCreateImage(cvGetSize(source),IPL_DEPTH_8U,1);
  cvCvtColor(source, im_gray, CV_RGB2GRAY);
  
  //cvSmooth( im_gray, im_gray, CV_GAUSSIAN, 3, 3, 0, 0 );
  //cvShowImage("Gray", im_gray);
  
  IplImage* im_bw = cvCreateImage(cvGetSize(im_gray),IPL_DEPTH_8U,1);
  cvThreshold(im_gray, im_bw, bw_threshold, 255, 3);
  
  //int morph_size = 2;
  //Mat element = getStructuringElement( MORPH_RECT, Size( 2*morph_size + 1, 2*morph_size+1 ), Point( morph_size, morph_size ) );
  //morphologyEx( im_bw, im_bw, MORPH_OPEN, element );
  
  cvDilate(im_bw, im_bw, NULL, 3);
  cvErode(im_bw, im_bw, NULL, 2);
  
  //correctPerspective(im_bw);
  //cvSmooth( im_bw, im_bw, CV_GAUSSIAN, 3, 3, 0, 0 );
  //try {
  //  cvShowImage("BW", im_bw);
  //} catch(...) {cout << "Could not display image" << endl;}
  return im_bw;
}

calibrationResult MVision::calibrate() 
{
  return calibrate(4);
}

calibrationResult MVision::calibrate(int target_points) 
{
  cout << ">> Starting calibration, target " << target_points << " points" << endl;
  int ok = 0;
  int tries = 0;
  calibrationResult result;
  do {
    result = testCalibration(cv_cap, target_points);
    switch(result.ok) 
      {
      case 0:
	cout << "Detected " << target_points << " points, exiting calibration.." << endl;
	ok = 1;
	break;
      case 1:
	cout << "Too many points detected, changing parameters.." << endl;
	if(cd_threshold < 300) {
	  cd_threshold += 10;
	} else if(max_rad > 10) {
	  max_rad--;
	} else if(acc_threshold < 40) {
	  acc_threshold++;
	} else {
	  ok = -1;
	}
	break;
      case -1:
	cout << "Too few points detected, changing parameters.." << endl;
	if(cd_threshold > 50) {
	  cd_threshold -= 10;
	} else if(max_rad < 30) {
	  max_rad++;
	} else if(acc_threshold > 5) {
	  acc_threshold--;
	} else {
	  ok = -1;
	}
	break;
      }
    
    tries++;
    //printDetectionParameters();
    cvWaitKey(10);
  } while(ok == 0);
  
  cout << endl <<  "Calibration ended, status: " << ok << endl;
  cout << "Found corners:" << endl;
  for(int i = 0; i < result.circles->total; i++) {
    float *p = (float*)cvGetSeqElem(result.circles, i);
    CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    cout << i << ": " << center.x << ", " << center.y << endl;
  }
  
  return result;
}

void MVision::printDetectionParameters() 
{
  cout << "iratio:        " << iratio << endl;
  cout << "min_dist:      " << min_dist << endl;
  cout << "cd_threshold:  " << cd_threshold << endl;
  cout << "acc_threshold: " << acc_threshold << endl;
  cout << "min_rad:       " << min_rad << endl;
  cout << "max_rad:       " << max_rad << endl;
}

calibrationResult MVision::testCalibration(CvCapture* cv_cap, int target_points) 
{
  cout << ">>> Testing calibration.." << endl;
  calibrationResult result;
  vector<CvSeq*> buffer;
  result.ok = 0;
  int too_many = 0;
  int too_few = 0;
  for(int i = 0; i < 10; i++) {
    //cout << ".";
    IplImage* img = cvQueryFrame(cv_cap);
    IplImage* processed = processImage(img);
    CvSeq* tmp = findCircles(processed);
    buffer.push_back(tmp);
    visualizeDetection(img, tmp, "calibration");
    if(tmp->total > target_points) {
      too_many++;
    } else if(tmp->total < target_points) {
      too_few++;
    }
    cvWaitKey(100);
  }
  
  if(too_many >= 2 && too_many > too_few) {
    result.ok = 1;
  } else if(too_few >= 2) {
    result.ok = -1;
  } else {
  }
  float x = 0;
  float y = 0;
  int i = 0;
  for(CvSeq* seq: buffer) {
    float *p = (float*)cvGetSeqElem(seq, 0);
    //CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    if(p != 0) {
      x += p[0];
      y += p[1];
      i++;
  
    } else {
      cout << "OP#(U¤=#)/¤(!" << endl;
    }
  }
  x = cvRound(x / i);
  y = cvRound(y / i);
  cout << ">>> Averages -> x: " << x << " - y: " << y << endl;
  return result;
}

IplImage* MVision::getImage() 
{
  return processImage(cvQueryFrame(cv_cap));
}

IplImage* MVision::getSubImage()
{
	IplImage* img = cvQueryFrame(cv_cap);
	IplImage* cor;
	img = correctPerspective(img, cor);
	return cor;
}

IplImage* MVision::getProcessedSubImage()
{
	IplImage* img = getImage();
	IplImage* cor;
	img = correctPerspective(img, cor);
	return cor;
}

CvSeq* MVision::findCircles(IplImage* source) 
{
  CvMemStorage* storage = cvCreateMemStorage(0);
  //cvThreshold( color_img, dst, threshold_value, max_BINARY_value,threshold_type );
  CvSeq* circles = cvHoughCircles(source, storage, CV_HOUGH_GRADIENT,
				  iratio,      // inverse ratio of the accumulator resolution
				  min_dist,      // minimum distance between circle centres
				  cd_threshold,    // higher threshold value for Canny
				  acc_threshold,     // accumulator threshold for the circle centers; smaller->more false circles
				  min_rad,  // minimum radius
				  max_rad);   // maximum radius
  //cout << "Found circles: " << circles->total << endl;
  return circles;
}

CvPoint MVision::findCircle() 
{
  vector<CvPoint*> buffer;
  int i = 0;
  float x = 0;
  float y = 0;
  while(i < 10) {
    CvSeq* circle = findCircles(1);
    float *p = (float*)cvGetSeqElem(circle, 0);
    //CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    if(p != 0) {
      //      buffer.push_back(cvPoint(cvRound(p[0]), cvRound(p[1])));
      x += p[0];
      y += p[1];
      i++;
    } else {
      cout << "..." << endl;
    }
  }
  return cvPoint(cvRound(x/i), cvRound(y/i));
}

/*
CvPoint findPointForCorner(StageCorner* corner)
{
  CvPoint point = findCircle();
  corner.setX(point.x);
  corner.setY(point.y);
  return point;
}
*/

CvSeq* MVision::findCircles(int target_count) 
{
  CvSeq* circles = findCircles(getImage());
  if(circles->total != target_count) {
    calibrate(target_count);
    return findCircles(target_count);
  } else {
    return circles;
  }
}

