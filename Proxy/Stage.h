#ifndef STAGE_H
#define STAGE_H

#include <vector>
#include "StageCorner.h"
#include "Robot.h"

class Stage 
{
  int stageWidthCm;
  int stageHeightCm;
  int stageWidthPx;
  int stageHeightPx;

  StageCorner *topLeft;
  StageCorner *topRight;
  StageCorner *bottomLeft;
  StageCorner *bottomRight;

  vector<Robot> robots;

  void updateStage();

 public:
  bool calibrated;
  const static int TOPLEFT = 1;
  const static int TOPRIGHT = 2;
  const static int BOTTOMLEFT = 3;
  const static int BOTTOMRIGHT = 4;
  Stage();
  void calibrateStage();
  void setBluetoothManager(BluetoothManager *btman);
  void setMVision(MVision *mv);
  BluetoothManager* getBluetoothManager();
  MVision* getMVision();
  void setTopLeftCorner(StageCorner *c);
  void setTopRightCorner(StageCorner *c);
  void setBottomRightCorner(StageCorner *c);
  void setBottonLeftCorner(StageCorner *c);
  StageCorner* getTopLeftCorner();
  StageCorner* getTopRightCorner();
  StageCorner* getBottomLeftCorner();
  StageCorner* getBottomRightCorner();
  vector<StageCorner> getCorners();
  bool cornersOn();
  bool cornerOn(int corner);
  bool cornersOff();
  bool cornerOff(int corner);
  bool cornersOk();
  void setWidthCm(int width);
  void setHeightCm(int height);
  int getXCm(int xPx);
  int getYCm(int yPx);
  void print();
};

#endif
