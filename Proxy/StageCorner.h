#include "BluetoothConnection.h"
#include "MVision.h"
#include "StagePoint.h"

//#include ""
class StageCorner
{
  StagePoint point;
  BluetoothConnection *connection;
 public:
  StageCorner();
  StageCorner(BluetoothConnection *c);
  void setBluetoothConnection(BluetoothConnection *c);
  BluetoothConnection* getBluetoothConnection();
  bool switchOn();
  bool switchOff();
  void setPoint(StagePoint p);
  void setWithCvPoint(CvPoint p);
  StagePoint getPoint();
  int getX();
  int getY();
};
