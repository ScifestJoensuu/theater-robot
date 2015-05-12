#include "BluetoothConnection.h"
//#include ""
class StageCorner
{
  int x;
  int y;
  BluetoothConnection *connection;
 public:
  StageCorner();
  StageCorner(BluetoothConnection *c);
  void setBluetoothConnection(BluetoothConnection *c);
  BluetoothConnection* getBluetoothConnection();
  bool switchOn();
  bool switchOff();
  void setX(int x);
  void setY(int y);
  void setWithCvPoint(CvPoint p);
  int getX();
  int getY();
};
