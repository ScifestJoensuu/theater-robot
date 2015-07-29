
#include "MVision.h"

int threshold_value = 0;
int threshold_type = 3;
int const max_BINARY_value = 255;

int bw_threshold = 250;
int iratio = 2;
int min_dist = 50;
int cd_threshold = 150;
int acc_threshold = 18;
int min_rad = 2;
int max_rad = 20;

CvCapture* cv_cap;

IplImage* currentImage;
IplImage* currentProcessed;

struct sysinfo sinfo;

MVision::MVision() 
{

}

int MVision::init(int camera) 
{
  stageCalibrated = false;
  calibrated = false;

  cout << ">> Initializing MVision-module" << endl;	
  cout << "OpenCV verision: " << CV_VERSION << endl;
  cout << "Using camera '" << camera << "'" << endl;
    
  int c;
  IplImage* color_img;
  cv_cap = cvCaptureFromCAM(camera);

  cvNamedWindow("Detection", CV_WINDOW_AUTOSIZE);
  cvMoveWindow("Detection", 30, 30);
  cvNamedWindow("Stage", CV_WINDOW_AUTOSIZE);
  cvMoveWindow("Stage", 750, 30);

    
  return 0;
}

void MVision::setTopLeft(StagePoint p)
{
  topLeft = p;
}

void MVision::setTopRight(StagePoint p)
{
  topRight = p;
}

void MVision::setBottomRight(StagePoint p)
{
  bottomRight = p;
}

void MVision::setBottomLeft(StagePoint p)
{
  bottomLeft = p;
}

void MVision::setStageWidth(int w)
{
  stageWidth = w;
}

void MVision::setStageHeight(int h)
{
  stageHeight =  h;
}

bool MVision::showImage()
{
  IplImage* tmp = cvQueryFrame(cv_cap);
  IplImage* bw;

  processImage(&tmp, &bw);
  
  CvMemStorage* storage = cvCreateMemStorage(0);  
  CvSeq* circles = findCircles(bw, storage);
  //  findCircles(bw, &circles);
  
  drawStage(tmp);
  visualizeDetection(tmp, circles, "Detection");
  
  
  cvShowImage("Detection", currentImage);

  cvClearMemStorage(storage);
  cvReleaseMemStorage(&storage);

  cvReleaseImageHeader(&currentImage);
  cvReleaseImageHeader(&tmp);
  cvReleaseImage(&bw);

  free(currentImage);
  free(tmp);
  free(bw);

  //cvWaitKey(10);
  
  return true;
}

void MVision::drawStage(IplImage* source)
{
  if(stageCalibrated) {
    CvPoint tl = cvPoint(topLeft.x ,topLeft.y);
    CvPoint tr = cvPoint(topRight.x ,topRight.y);
    CvPoint bl = cvPoint(bottomLeft.x ,bottomLeft.y);
    CvPoint br = cvPoint(bottomRight.x ,bottomRight.y);
    cvCircle(source, tl, 10, CV_RGB(255,0,0), 1, CV_AA, 0);
    cvCircle(source, tr, 10, CV_RGB(255,0,0), 1, CV_AA, 0);
    cvCircle(source, bl, 10, CV_RGB(255,0,0), 1, CV_AA, 0);
    cvCircle(source, br, 10, CV_RGB(255,0,0), 1, CV_AA, 0);
    CvFont font;
    cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.5,0.5);  
    cvPutText(source, "tl", cvPoint(tl.x+20, tl.y+5), &font, CV_RGB(255,0,0));
    cvPutText(source, "tr", cvPoint(tr.x+20, tr.y+5), &font, CV_RGB(255,0,0));
    cvPutText(source, "bl", cvPoint(bl.x+20, bl.y+5), &font, CV_RGB(255,0,0));
    cvPutText(source, "br", cvPoint(br.x+20, br.y+5), &font, CV_RGB(255,0,0));
  }
}

void MVision::setRobots(vector<Robot*> r)
{
  robots = r;
}

bool MVision::dismisImage()
{
  //cvDestroyWindow("Detection");
  //cvReleaseImage(&currentImage);
}

void MVision::testi(IplImage* a, IplImage* b)
{
  a = b;
}
bool MVision::showStageImage()
{
  return false;
  IplImage* tmp = cvQueryFrame(cv_cap);
  IplImage* target;
  correctPerspective(tmp, target);
  return true;
}

void MVision::visualizeStage(IplImage* source, CvSeq* detection, string title)
{
  CvFont font;
  cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.5,0.5);  
  if(detection->total == 0) {
    cvPutText(source, "no points detected", cvPoint(10,20), &font, CV_RGB(0,255,0));
  }
  for(Robot* r: robots) {
      if(r->getX() < 0 && r->getY() < 0) continue;
      CvPoint p = cvPoint(r->getX(), r->getY());
      Robot::Color c = r->getColor();
      vector<StagePoint> positions = r->getPositions();
      for(StagePoint point: positions) {
	CvPoint cvpoint = cvPoint(point.x, point.y);
	cvCircle(source, cvpoint, 2, CV_RGB(c.r, c.g, c.b), 1, CV_AA, 0);
      }
      StagePoint chk_sp = r->firstPointFromCheckpoint();
      CvPoint chk_point = cvPoint(chk_sp.x + 5, chk_sp.y + 2);
      CvPoint id_point = cvPoint(p.x+5, p.y+5);
      cvPutText(source, "chk", chk_point, &font, CV_RGB(c.r,c.g,c.b));
      cvPutText(source, r->getId().c_str(), id_point, &font, CV_RGB(c.r,c.g,c.b));
      cvCircle(source, p, 10, CV_RGB(c.r, c.g, c.b), 1, CV_AA, 0);
    }

  for (int i = 0; i < detection->total; i++) {
    float *p = (float*)cvGetSeqElem(detection, i);
    CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    CvScalar val = cvGet2D(source, center.y, center.x);
    int x;
    int y;
    if(center.x > stageWidth/2) {
      x = center.x - 90;
    } else {
      x = center.x + 20;
    }
    if(center.y > stageHeight/2) {
      y = center.y - 5;
    } else {
      y = center.y + 10;
    }
    CvPoint txt_point = cvPoint(x, y);
    string txt = (to_string(center.x) + ", " + to_string(center.y));

    cvCircle(source, center, cvRound(p[2]+5), CV_RGB(0,255,0), 1, CV_AA, 0);
    cvPutText(source, txt.c_str(), txt_point, &font, CV_RGB(0,255,0));
  }
  // currentImage = source;
  /*
  IplImage* tmp;
  getStageImage(tmp);
  for (vector<Robot*>::iterator i = robots.begin(); i != robots.end(); i++) {
    Robot* r = *i;
    CvPoint center = cvPoint(r->getPosition().x, r->getPosition().y);
    CvScalar val = cvGet2D(tmp, center.y, center.x);
    cvCircle(tmp, center, 10, CV_RGB(r->getColor().r,r->getColor().g,r->getColor().b), 1, CV_AA, 0);
  }
  cvShowImage("Stage", tmp);
  */
}

void MVision::visualizeDetection(IplImage* source, CvSeq* detection, string title)
{
  sysinfo(&sinfo);
  //  long long virtualMemUsed = sinfo.totalram - sinfo.freeram;
  CvFont font;
  cvInitFont(&font, CV_FONT_HERSHEY_SIMPLEX, 0.5,0.5);  
  cvPutText(source, ("free ram: " + to_string(sinfo.freeram/1000000)).c_str(), cvPoint(10,20), &font, CV_RGB(0,255,0));
  if(detection->total == 0) {
    cvPutText(source, "no points detected", cvPoint(10,40), &font, CV_RGB(0,255,0));
  }
  for (int i = 0; i < detection->total; i++) {
    float *p = (float*)cvGetSeqElem(detection, i);
    CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    CvScalar val = cvGet2D(source, center.y, center.x);
    int x;
    int y;
    if(center.x > stageWidth/2) {
      x = center.x - 90;
    } else {
      x = center.x + 20;
    }
    if(center.y > stageHeight/2) {
      y = center.y - 5;
    } else {
      y = center.y + 10;
    }
    CvPoint txt_point = cvPoint(x, y);
    string txt = (to_string(center.x) + ", " + to_string(center.y));

    cvCircle(source, center, cvRound(p[2]+5), CV_RGB(0,255,0), 1, CV_AA, 0);
    cvPutText(source, txt.c_str(), txt_point, &font, CV_RGB(0,255,0));
  }
  currentImage = source;
}


IplImage* MVision::correctPerspective(IplImage* source, IplImage* target)
{
  cout << "Correcting perspective" << endl;
  Mat src = cvarrToMat(source);
  Mat quad = Mat::zeros(stageHeight, stageWidth, CV_8UC3);  
  vector<Point2f> corners = detectCorners();

  if(corners.size() == 4) {
    // Corners of the destination image
    std::vector<cv::Point2f> quad_pts;
    quad_pts.push_back(cv::Point2f(0, 0));
    quad_pts.push_back(cv::Point2f(quad.cols, 0));
    quad_pts.push_back(cv::Point2f(quad.cols, quad.rows));
    quad_pts.push_back(cv::Point2f(0, quad.rows));
    
    // Get transformation matrix
    Mat transmtx = getPerspectiveTransform(corners, quad_pts);
    
    // Apply perspective transformation
    warpPerspective(src, quad, transmtx, quad.size());
    IplImage t = quad;
    cout << "Perspectice corrected.. " << endl;
    
    currentCorrected = &t;
    return currentCorrected;
  } else {
    cout << "Corner detection failed.." << endl;
  }
  return nullptr;
}

bool wayToSort(Point2f i, Point2f j) 
{ 
  return i.y < j.y; 
}

vector<Point2f> MVision::detectCorners() 
{
  vector<Point2f> corners;
  corners.push_back(Point2f(topLeft.x, topLeft.y));
  corners.push_back(Point2f(topRight.x, topRight.y));
  corners.push_back(Point2f(bottomRight.x, bottomRight.y));
  corners.push_back(Point2f(bottomLeft.x, bottomLeft.y));
  return corners;
}

IplImage* MVision::processImage(IplImage** source, IplImage** target) 
{
  IplImage *im_gray = cvCreateImage(cvGetSize(*source),IPL_DEPTH_8U,1);
  cvCvtColor(*source, im_gray, CV_RGB2GRAY);
  *target = cvCreateImage(cvGetSize(im_gray),IPL_DEPTH_8U,1);
  cvThreshold(im_gray, *target, bw_threshold, 255, 3);

  cvDilate(*target, *target, NULL, 4);
  cvErode(*target, *target, NULL, 2);

  cvReleaseImage(&im_gray);
  free(im_gray);
  
  return *target;
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
    printDetectionParameters();
    cvWaitKey(10);
  } while(ok == 0);
  
  cout << endl <<  "Calibration ended, status: " << ok << endl;

  /*
    cout << "Found corners:" << endl;
    for(int i = 0; i < result.circles->total; i++) {
    float *p = (float*)cvGetSeqElem(result.circles, i);
    CvPoint center = cvPoint(cvRound(p[0]),cvRound(p[1]));
    cout << i << ": " << center.x << ", " << center.y << endl;
    }
  */

  calibrated = result.ok;
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
  int count = 0;
  while(count < 5) {
    IplImage* img = cvQueryFrame(cv_cap);
    IplImage* processed;
    processImage(&img, &processed);
    
    CvMemStorage* storage = cvCreateMemStorage(0);
    CvSeq* tmp = findCircles(processed, storage);
    if(tmp != nullptr) {
      buffer.push_back(tmp);
      count++;
    }
    visualizeDetection(img, tmp, "calibration");
    
    cvClearMemStorage(storage);
    cvReleaseMemStorage(&storage);

    if(tmp->total > target_points) {
      too_many++;
    } else if(tmp->total < target_points) {
      too_few++;
    }
    cvWaitKey(10);
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
  //cout << ">>> Averages -> x: " << x << " - y: " << y << endl;
  return result;
}

IplImage* MVision::getImage() 
{
  IplImage* tmp;
  IplImage* source = cvQueryFrame(cv_cap);
  return processImage(&source, &tmp);
}

IplImage* MVision::getStageImage(IplImage* cor)
{
  return new IplImage();
}

IplImage* MVision::getProcessedStageImage()
{
  IplImage* img = getImage();
  IplImage* cor;
  correctPerspective(img, cor);
  return cor;
}

void MVision::findCircles(IplImage* source, CvSeq** seq, CvMemStorage* storage)
{
  *seq = cvHoughCircles(source, storage, CV_HOUGH_GRADIENT,
				  iratio,      // inverse ratio of the accumulator resolution
				  min_dist,      // minimum distance between circle centres
				  cd_threshold,    // higher threshold value for Canny
				  acc_threshold,     // accumulator threshold for the circle centers; smaller->more false circles
				  min_rad,  // minimum radius
				  max_rad);   // maximum radius
}

CvSeq* MVision::findCircles(IplImage* source, CvMemStorage* storage) 
{
  CvSeq* circles = cvHoughCircles(source, storage, CV_HOUGH_GRADIENT,
				  iratio,      // inverse ratio of the accumulator resolution
				  min_dist,      // minimum distance between circle centres
				  cd_threshold,    // higher threshold value for Canny
				  acc_threshold,     // accumulator threshold for the circle centers; smaller->more false circles
				  min_rad,  // minimum radius
				  max_rad);   // maximum radius
  //  cvClearMemStorage(storage);
  //cvReleaseMemStorage(&storage);
  return circles;
}

StagePoint MVision::findCircle()
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
  return StagePoint(cvRound(x/i), cvRound(y/i));
}

StagePoint MVision::findCircleFromStage()
{
  return findCircleFromStage(0);
}
StagePoint MVision::findCircleFromStage(int tries)
{
  IplImage* source = cvQueryFrame(cv_cap);
  Mat src = cvarrToMat(source);
  Mat quad = Mat::zeros(stageHeight, stageWidth, CV_8UC3);
  
  vector<Point2f> corners = detectCorners();

  if(corners.size() == 4) {
    // Corners of the destination image
    std::vector<cv::Point2f> quad_pts;
    quad_pts.push_back(cv::Point2f(0, 0));
    quad_pts.push_back(cv::Point2f(quad.cols, 0));
    quad_pts.push_back(cv::Point2f(quad.cols, quad.rows));
    quad_pts.push_back(cv::Point2f(0, quad.rows));
    
    // Get transformation matrix
    Mat transmtx = getPerspectiveTransform(corners, quad_pts);
    
    // Apply perspective transformation
    warpPerspective(src, quad, transmtx, quad.size());

    IplImage corrected = quad;
    
    //cout << "Perspectice corrected.. " << endl;
    //currentCorrected = corrected;

    IplImage* bw;
    IplImage* c = &corrected;
    processImage(&c, &bw);
    //    cout << "finding circles" << endl;
    //CvSeq* circles = findCircles(bw, 1, 0);
    CvMemStorage* storage = cvCreateMemStorage(0);
    CvSeq* circles = findCircles(bw, storage);
    visualizeStage(&corrected, circles, "Stage");
    cvShowImage("Stage", &corrected);
    if(circles->total == 1) {
        float *p = (float*)cvGetSeqElem(circles, 0);
	if(p != nullptr) {
	  StagePoint center = StagePoint(cvRound(p[0]),cvRound(p[1]));
	  return center;
	}
    }
    cvClearMemStorage(storage);
    cvReleaseMemStorage(&storage);

    cvReleaseImage(&bw);
    free(bw);
    //cvShowImage("track", &corrected);
  } else {
    cout << "Corner detection failed.." << endl;
  }
  cvReleaseImageHeader(&source);
  free(source);
  if(tries > 3) 
    return StagePoint();
  else 
    return findCircleFromStage(++tries);
}

vector<StagePoint> MVision::findCirclesFromStage()
{
  vector<StagePoint> positions;
  IplImage* source = cvQueryFrame(cv_cap);

  Mat src = cvarrToMat(source);
  Mat quad = Mat::zeros(stageHeight, stageWidth, CV_8UC3);
  
  vector<Point2f> corners = detectCorners();

  if(corners.size() == 4) {
    // Corners of the destination image
    std::vector<cv::Point2f> quad_pts;
    quad_pts.push_back(cv::Point2f(0, 0));
    quad_pts.push_back(cv::Point2f(quad.cols-1, 0));
    quad_pts.push_back(cv::Point2f(quad.cols-1, quad.rows-1));
    quad_pts.push_back(cv::Point2f(0, quad.rows-1));
    
    // Get transformation matrix
    Mat transmtx = getPerspectiveTransform(corners, quad_pts);
    
    // Apply perspective transformation
    warpPerspective(src, quad, transmtx, quad.size());

    IplImage corrected = quad;
    //currentCorrected = &corrected;

    IplImage* bw;
    IplImage* c = &corrected;
    processImage(&c, &bw);
    CvMemStorage* storage = cvCreateMemStorage(0);
    CvSeq* circles = findCircles(bw, storage);
    visualizeStage(&corrected, circles, "Stage");
    cvShowImage("Stage", &corrected);

    for (int i = 0; i < circles->total; i++) {
      float *p = (float*)cvGetSeqElem(circles, i);
      StagePoint center = StagePoint(cvRound(p[0]),cvRound(p[1]));
      positions.push_back(center);
      //CvScalar val = cvGet2D(source, center.y, center.x);
      //if (val.val[0] < 1) continue;
      //cvCircle(source, center, cvRound(p[2]+5), CV_RGB(0,255,0), 1, CV_AA, 0);
    }
        
    cvClearMemStorage(storage);
    cvReleaseMemStorage(&storage);

    cvReleaseImage(&bw);
    free(bw);
  } else {
    cout << ">>> Corner detection failed.." << endl;
  }
  cvReleaseImageHeader(&source);
  free(source);
  return positions;
}

CvSeq* MVision::findCircles(int target_count) 
{
  return findCircles(target_count, 0);
}

CvSeq* MVision::findCircles(int target_count, int try_count) 
{
  IplImage* tmp = getImage();
  return findCircles(tmp, target_count, try_count);
}

CvSeq* MVision::findCircles(IplImage* source, int target_count, int try_count) 
{
  CvMemStorage* storage = cvCreateMemStorage(0);
  CvSeq* circles = findCircles(source, storage);
  visualizeDetection(source, circles, "Detected");
  if(circles->total != target_count) {
    if(try_count > 5) {
          calibrate(target_count);
      try_count = 0;
    }
    try_count++;
    cvClearMemStorage(storage);
    cvReleaseMemStorage(&storage);
      return findCircles(target_count, try_count);
  } else {
    cvClearMemStorage(storage);
    cvReleaseMemStorage(&storage);
    return circles;
  }
}

