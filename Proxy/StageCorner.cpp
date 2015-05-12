//stagecorner.cpp

#include "StageCorner.h"

StageCorner::StageCorner()
{
}

StageCorner::StageCorner(BluetoothConnection *bt)
{
  connection = bt;
}

void StageCorner::setWithCvPoint(CvPoint p)
{
	x = p.x;
	y = p.y;
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
