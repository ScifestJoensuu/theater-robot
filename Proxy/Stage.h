#ifndef STAGE_H
#define STAGE_H

#include <vector>
#include "StagePoint.h"
#include "StageCorner.h"
#include "Robot.h"
#include "BluetoothManager.h"

using namespace std;

class Stage 
{
  const static int no_such_robot = 1;
 
  StageCorner *topLeft;
  StageCorner *topRight;
  StageCorner *bottomLeft;
  StageCorner *bottomRight;

  vector<Robot*> robots;

  void updateStage();
  BluetoothManager *btman;
  MVision* mvision;
  Robot::Color getColorForRobot();

 public:
  bool calibrated;

  const static int TOPLEFT = 1;
  const static int TOPRIGHT = 2;
  const static int BOTTOMLEFT = 3;
  const static int BOTTOMRIGHT = 4;

  int stageWidthCm;
  int stageHeightCm;
  int stageWidthPx;
  int stageHeightPx;


  Stage();
  void calibrateStage();
  void calibrateRobot(Robot* r);
  void print();
  void printRobots();

  void setBluetoothManager(BluetoothManager *btman);
  void setMVision(MVision *mv);
  void setWidthCm(int width);
  void setHeightCm(int height);
  void setTopLeftCorner(StageCorner *c);
  void setTopRightCorner(StageCorner *c);
  void setBottomRightCorner(StageCorner *c);
  void setBottonLeftCorner(StageCorner *c);

  BluetoothManager* getBluetoothManager();
  MVision* getMVision();
  int getXCm(int xPx);
  int getYCm(int yPx);
  StageCorner* getTopLeftCorner();
  StageCorner* getTopRightCorner();
  StageCorner* getBottomLeftCorner();
  StageCorner* getBottomRightCorner();
  vector<StageCorner> getCorners();

  vector<StagePoint> findObjects();
  vector<StagePoint> findRobots();
  StagePoint findRobot(string id);
  StagePoint findRobot(Robot* r);
  bool robotAtPoint(string id, StagePoint* p);
  bool robotAtPoint(Robot* r, StagePoint* p);
  Robot* getRobot(string id);
  vector<Robot*> getRobots();
  bool turnRobotTowards(Robot* r, StagePoint p);

  bool cornersOn();
  bool cornerOn(int corner);
  bool cornersOff();
  bool cornerOff(int corner);
  bool cornersOk();
};

#endif
