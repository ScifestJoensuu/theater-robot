
#include "Stage.h"

Stage::Stage()
{
  topLeft = new StageCorner();
  topRight = new StageCorner();
  bottomLeft = new StageCorner();
  bottomRight = new StageCorner();
}

bool Stage::cornersOn() 
{
  cout << ">> Switching all corners on" << endl;
  bool ok = false;
  if(cornersOk()) {
    cout << "->BL" << endl;
    ok = bottomLeft->switchOn();
    if(!ok) return false;
    cout << "->BR" << endl;
    ok = bottomRight->switchOn();
    if(!ok) return false;
    cout << "->TL" << endl;
    ok = topLeft->switchOn();
    if(!ok) return false;
    cout << "->TR" << endl;
    ok = topRight->switchOn();
    return ok;
  }
  return false;
}

bool Stage::cornersOff()
{
  cout << ">> Switching all corners off" << endl;
  bool ok = false;
  if(cornersOk()) {
    ok = bottomLeft->switchOff();
    if(!ok) return false;
    ok = bottomRight->switchOff();
    if(!ok) return false;
    ok = topLeft->switchOff();
    if(!ok) return false;
    ok = topRight->switchOff();
    return ok;
  }
  return false;
}

bool Stage::cornerOn(int corner)
{
  bool ok = false;
  switch(corner)
    {
    case TOPLEFT:
      ok = topLeft->switchOn();
      break;
    case TOPRIGHT:
      ok = topRight->switchOn();
      break;
    case BOTTOMLEFT:
      ok = bottomLeft->switchOn();
      break;
    case BOTTOMRIGHT:
      ok = bottomRight->switchOn();
      break;
    default:
      break;
    }
  return ok;
}

bool Stage::cornerOff(int corner)
{
  bool ok = false;
  switch(corner)
    {
    case TOPLEFT:
      ok = topLeft->switchOff();
      break;
    case TOPRIGHT:
      ok = topRight->switchOff();
      break;
    case BOTTOMLEFT:
      ok = bottomLeft->switchOff();
      break;
    case BOTTOMRIGHT:
      ok = bottomRight->switchOff();
      break;
    default:
      break;
    }
  return ok;
}

bool Stage::cornersOk()
{
  if(topLeft->getBluetoothConnection()->getStatus() == -1) return false;
  if(topRight->getBluetoothConnection()->getStatus() == -1) return false;
  if(bottomLeft->getBluetoothConnection()->getStatus() == -1) return false;
  if(bottomRight->getBluetoothConnection()->getStatus() == -1) return false;
  return true;
}

StageCorner* Stage::getTopLeftCorner()
{
  return topLeft;
}

StageCorner* Stage::getTopRightCorner()
{
  return topRight;
}

StageCorner* Stage::getBottomLeftCorner()
{
  return bottomLeft;
}

StageCorner* Stage::getBottomRightCorner()
{
  return bottomRight;
}
