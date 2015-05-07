#include "BluetoothConnection.h"

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
  
};
