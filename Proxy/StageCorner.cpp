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
	point.x = p.x;
	point.y = p.y;
}

void StageCorner::setPoint(StagePoint p)
{
	point = p;
}

StagePoint StageCorner::getPoint()
{
	return point;
}

int StageCorner::getX()
{
	return point.x;
}

int StageCorner::getY()
{
	return point.y;
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
