//stagecorner.cpp

#include "StageCorner.h"

StageCorner::StageCorner()
{
}

StageCorner::StageCorner(string n)
{
  name = n;
}
StageCorner::StageCorner(BluetoothConnection *bt)
{
  connection = bt;
}

StageCorner::StageCorner(BluetoothConnection *bt, string n)
{
  connection = bt;
  name = n;
}

/*
void StageCorner::setWithCvPoint(CvPoint p)
{
	point.x = p.x;
	point.y = p.y;
}
*/
void StageCorner::setStagePoint(StagePoint p)
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
  cout << ">> Switching " << name << " on" << endl;
  bool ok = connection->sendMessageAndWaitForResponse("led_on");
  if(ok) cout << ">>> ... OK" << endl;
  else cout << ">>> ... FAIL" << endl;
  return ok;
}

bool StageCorner::switchOff()
{
  cout << ">> Switching " << name << " off" << endl;
  bool ok = connection->sendMessageAndWaitForResponse("led_off");
  if(ok) cout << ">>> ... OK" << endl;
  else cout << ">>> ... FAIL" << endl;
  return ok;
}
