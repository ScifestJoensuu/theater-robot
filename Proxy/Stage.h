#ifndef STAGE_H
#define STAGE_H

#include <vector>
#include "StageCorner.h"

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

  void updateStage();

 public:
  const static int TOPLEFT = 1;
  const static int TOPRIGHT = 2;
  const static int BOTTOMLEFT = 3;
  const static int BOTTOMRIGHT = 4;
  Stage();
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
  int getXCm(int xPx);
  int getYCm(int yPx);
};

#endif
