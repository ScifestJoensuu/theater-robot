
#include "Stage.h"

Stage::Stage()
{
  calibrated = false;
  topLeft = new StageCorner();
  topRight = new StageCorner();
  bottomLeft = new StageCorner();
  bottomRight = new StageCorner();
}

void Stage::setWidthCm(int width)
{
  stageWidthCm = width;
  stageWidthPx = width;
}

void Stage::setHeightCm(int height)
{
  stageHeightCm = height;
  stageHeightPx = height;
}

void Stage::setBluetoothManager(BluetoothManager* btm)
{
  btman = btm;
}

void Stage::setMVision(MVision* mv)
{
  mvision = mv;
}

void Stage::calibrateStage()
{
	for(int i = 0; i < 3; i++) {
		vector<BluetoothConnection*> devices = btman->scanDevices();
		for(vector<BluetoothConnection*>::iterator it = devices.begin(); it != devices.end(); ++it) {
			/* std::cout << *it; ... */
			BluetoothConnection* tmp = *it;
			const char* name = tmp->getName().c_str();
			if(strcmp(name, "StageCornerTL") == 0) {
				topLeft->setBluetoothConnection(tmp);
			} else if(strcmp(name, "StageCornerTR") == 0) {
				topRight->setBluetoothConnection(tmp);
			} else if(strcmp(name, "StageCornerBL") == 0) {
				bottomLeft->setBluetoothConnection(tmp);
			} else if(strcmp(name, "StageCornerBR") == 0) {
				bottomRight->setBluetoothConnection(tmp);
			}
		}
		if(this->cornersOk()) break;
	}
	if(!cornersOk()) {
		cout << ">> Did not find all of the corners, shutting down" << endl;
		exit(0);
	}

  cout << "TL: " << topLeft->getBluetoothConnection()->getAddress() << endl;
  cout << "TR: " << topRight->getBluetoothConnection()->getAddress() << endl;
  cout << "BL: " << bottomLeft->getBluetoothConnection()->getAddress() << endl;
  cout << "BR: " << bottomRight->getBluetoothConnection()->getAddress() << endl;

  bool ok = cornersOn();
  if(!ok) {
    cout << ">> Could not switch on corners, shutting down" << endl;
    exit(0);
  }

  //showImage();

  ok = cornersOff();
  for(int i = 0; i < 5; i++) {
    ok = cornerOn(TOPLEFT);
    sleep(1);
    ok = cornerOff(TOPLEFT);
    ok = cornerOn(TOPRIGHT);
    sleep(1);
    ok = cornerOff(TOPRIGHT);
    ok = cornerOn(BOTTOMRIGHT);
    sleep(1);
    ok = cornerOff(BOTTOMRIGHT);
    ok = cornerOn(BOTTOMLEFT);
    sleep(1);
    ok = cornerOff(BOTTOMLEFT);
    //cout << "next.." << endl;
    for(int j = 0; j < 3; j++) {
      ok = cornersOn();
      sleep(1);
      ok = cornersOff();
      sleep(1);
    }
  }
  cout << ">>> Tests ready" << endl;
  cout << ">> Finding corner coordinates for the stage.." << endl;

  CvPoint p;
  cornerOn(TOPLEFT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setTopLeft(p);
  topLeft->setWithCvPoint(p);
  cornersOff();
  sleep(1);

  cornerOn(TOPRIGHT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setTopRight(p);
  topRight->setWithCvPoint(p);
  cornersOff();
  sleep(1);

  cornerOn(BOTTOMRIGHT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setBottomRight(p);
  bottomRight->setWithCvPoint(p);
  cornersOff();
  sleep(1);

  cornerOn(BOTTOMLEFT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setBottomLeft(p);
  bottomLeft->setWithCvPoint(p);
  cornersOff();
  sleep(1);

  cout << ">>> Finding coordinates ready" << endl;
  print();

  calibrated = true;

  //  boolean ok = stage.cornerOn(stage.TOPLEFT);
  //  mvision.findCircle();

  cout << "Stage calibration ready" << endl;
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

/*
void Stage::updateValues()
{
  if(!cornersOk()) return;
  cout << "Corners ok.." << endl;
  //  stageWidthPx = topRight->getX() - topLeft->getX();
  //  stageHeightPx = 
}
*/

void Stage::print()
{
  cout << "Stage width:  " << stageWidthCm << "cm, " << stageWidthPx << "px" << endl;
  cout << "Stage height: " << stageHeightCm << "cm, " << stageHeightPx << "px" << endl;
  cout << "Top left:     " << topLeft->getX() << " " << topLeft->getY() << endl;
  cout << "Top right:    " << topRight->getX() << " " << topRight->getY() << endl;
  cout << "Bottom right: " << bottomRight->getX() << " " << bottomRight->getY() << endl;
  cout << "Bottom left:  " << bottomLeft->getX() << " " << bottomLeft->getY() << endl;
}
