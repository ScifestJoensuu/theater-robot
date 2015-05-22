
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
			string name = tmp->getName().c_str();
			if(name.compare("StageCornerTL") == 0) {
				topLeft->setBluetoothConnection(tmp);
			} else if(name.compare("StageCornerTR") == 0) {
				topRight->setBluetoothConnection(tmp);
			} else if(name.compare("StageCornerBL") == 0) {
				bottomLeft->setBluetoothConnection(tmp);
			} else if(name.compare("StageCornerBR") == 0) {
				bottomRight->setBluetoothConnection(tmp);
			} else if(name.length() > 11 && name.compare(0, 11, "ActorRobot:") == 0) {
				Robot* tmp_r = new Robot(tmp);
				tmp_r->setId(name.substr(11, string::npos));
				tmp_r->setColor(getColorForRobot());
				robots.push_back(tmp_r);

				cout << ">> Found a robot: " << name << endl;
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

  StagePoint p;
  cornerOn(TOPLEFT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setTopLeft(p);
  topLeft->setStagePoint(p);
  cornersOff();
  sleep(1);

  cornerOn(TOPRIGHT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setTopRight(p);
  topRight->setStagePoint(p);
  cornersOff();
  sleep(1);

  cornerOn(BOTTOMRIGHT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setBottomRight(p);
  bottomRight->setStagePoint(p);
  cornersOff();
  sleep(1);

  cornerOn(BOTTOMLEFT);
  sleep(1);
  p = mvision->findCircle();
  mvision->setBottomLeft(p);
  bottomLeft->setStagePoint(p);
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

bool Stage::robotAtPoint(string id, StagePoint* p)
{
	return robotAtPoint(getRobot(id), p);
}

bool Stage::robotAtPoint(Robot* r, StagePoint* p)
{
	if(r->getPosition().getDistance(p) <= 10) return true;
	return false;
}


Robot* Stage::getRobot(string id)
{
	for(vector<Robot*>::iterator it = robots.begin(); it != robots.end(); ++it) {
		Robot *tmp = *it;
		if(tmp->getId().compare(id) == 0) {
			return tmp;
		}
	}
	return nullptr;
}

StagePoint Stage::findRobot(string id)
{
	return findRobot(getRobot(id));
}
StagePoint Stage::findRobot(Robot* r)
{
	if(r != nullptr) {
		r->irOn();
		StagePoint point = mvision->findCircle();
		r->appendPosition(point);
		r->irOff();
		return point;
	}

	throw no_such_robot;
}

Robot::Color Stage::getColorForRobot()
{
	while (true) {
		int r = rand() % 256;
		int g = rand() % 256;
		int b = rand() % 256;
		bool ok = true;
		for(vector<Robot*>::iterator it = robots.begin(); it != robots.end(); it++) {
			Robot* tmp = *it;
			Robot::Color col = tmp->getColor();
			if(col.r == r || col.g == g || col.b == b) {
				ok = false;
				break;
			}
		}

		if(ok) {
			Robot::Color c;
			c.r = r;
			c.g = g;
			c.b = b;
			return c;
		}
	}
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
  if(topLeft == nullptr || topLeft->getBluetoothConnection()->getStatus() == -1) return false;
  if(topRight == nullptr || topRight->getBluetoothConnection()->getStatus() == -1) return false;
  if(bottomLeft == nullptr || bottomLeft->getBluetoothConnection()->getStatus() == -1) return false;
  if(bottomRight == nullptr || bottomRight->getBluetoothConnection()->getStatus() == -1) return false;
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
