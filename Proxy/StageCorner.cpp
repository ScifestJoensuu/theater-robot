//stagecorner.cpp

#include "StageCorner.h"

StageCorner::StageCorner()
{
}

StageCorner::StageCorner(BluetoothConnection *bt)
{
  connection = bt;
}

void StageCorner::setBluetoothConnection(BluetoothConnection *bt)
{
  connection = bt;
}

BluetoothConnection* StageCorner::getBluetoothConnection()
{
  return connection;
}

bool StageCorner::switchOn()
{
  return connection->sendMessageAndWaitForResponse("led_on");
}

bool StageCorner::switchOff()
{
  return connection->sendMessageAndWaitForResponse("led_off");
}
