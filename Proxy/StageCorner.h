#include "BluetoothConnection.h"
#include "MVision.h"
#include "StagePoint.h"

//#include ""
class StageCorner
{
  string name;
  StagePoint point;
  BluetoothConnection *connection;
 public:
  StageCorner();
  StageCorner(string name);
  StageCorner(BluetoothConnection *c);
  StageCorner(BluetoothConnection *c, string name);
  void setBluetoothConnection(BluetoothConnection *c);
  BluetoothConnection* getBluetoothConnection();
  bool switchOn();
  bool switchOff();
  void setStagePoint(StagePoint p);
  //void setWithCvPoint(CvPoint p);
  StagePoint getPoint();
  int getX();
  int getY();
};
