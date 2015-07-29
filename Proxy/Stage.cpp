
#include "Stage.h"

Stage::Stage()
{
  calibrated = false;
  topLeft = new StageCorner("TopLeft");
  topRight = new StageCorner("TopRight");
  bottomLeft = new StageCorner("BottomLeft");
  bottomRight = new StageCorner("BottomRight");
  
  setWidthCm(250);
  setHeightCm(200);
}

void Stage::setWidthCm(int width)
{
  stageWidthCm = width;
  stageWidthPx = 400;
}

void Stage::setHeightCm(int height)
{
  stageHeightCm = height;
  stageHeightPx = 300;
}

void Stage::setBluetoothManager(BluetoothManager* btm)
{
  btman = btm;
}

void Stage::setMVision(MVision* mv)
{
  mvision = mv;
  mvision->setStageWidth(stageWidthPx);
  mvision->setStageHeight(stageHeightPx);
}

void Stage::calibrateStage()
{
  for(int i = 0; i < 3; i++) {
    
    vector<BluetoothConnection*> devices;
    if(false) {
      devices.push_back(new BluetoothConnection("StageCornerBL", "20:14:05:05:18:79"));
      devices.push_back(new BluetoothConnection("StageCornerTL", "20:14:05:05:21:01"));
      devices.push_back(new BluetoothConnection("StageCornerTR", "20:14:05:05:26:93"));
      devices.push_back(new BluetoothConnection("StageCornerBR", "20:14:05:05:12:34"));
      devices.push_back(new BluetoothConnection("ActorRobot1", "98:D3:31:60:2B:B4"));
    } else {
      devices = btman->scanDevices();
    }
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
      } else if(name.length() >= 10 && name.compare(0, 10, "ActorRobot") == 0) {
	Robot* tmp_r = new Robot(tmp);
	tmp_r->setId(name.substr(10, string::npos));
	tmp_r->setColor(getColorForRobot());
	robots.push_back(tmp_r);

	cout << ">> Found a robot: " << name << endl;
      } else {
	cout << "Unrecognized device: " << name << endl;
      }
    }
    //exit(0);
    if(this->cornersOk()) break;
  }
  if(!cornersOk()) {
    cout << ">> Did not find all of the corners, shutting down" << endl;
    exit(0);
  }

  /*
  cout << ">>>TL: " << topLeft->getBluetoothConnection()->getAddress() << endl;
  cout << ">>>TR: " << topRight->getBluetoothConnection()->getAddress() << endl;
  cout << ">>>BL: " << bottomLeft->getBluetoothConnection()->getAddress() << endl;
  cout << ">>>BR: " << bottomRight->getBluetoothConnection()->getAddress() << endl;
  */ 

  cout << ">> Running tests.." << endl;
  for(Robot* r: robots) {
    r->irOff();
  }
  
  bool ok = cornersOn();
  if(!ok) {
    cout << ">> Could not switch on corners, shutting down" << endl;
    exit(0);
  }

  //showImage();
  
  ok = cornersOff();
  for(int i = 0; i < 0; i++) {
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

  for(Robot* r: robots) {
    r->irOn();
    sleep(1);
    r->irOff();
    sleep(1);
  }

  mvision->setRobots(robots);

  cout << ">>> Tests ready" << endl;
  
  cout << ">> Finding corner coordinates for the stage.." << endl;

  //cornersOn();
  //  mvision->calibrate();
  cornersOff();

  StagePoint p;
  cornerOn(TOPLEFT);
  //sleep(1);
  p = mvision->findCircle();
  mvision->setTopLeft(p);
  topLeft->setStagePoint(p);
  cornersOff();
  //sleep(1);

  cornerOn(TOPRIGHT);
  //sleep(1);
  p = mvision->findCircle();
  mvision->setTopRight(p);
  topRight->setStagePoint(p);
  cornersOff();
  //sleep(1);

  cornerOn(BOTTOMRIGHT);
  //sleep(1);
  p = mvision->findCircle();
  mvision->setBottomRight(p);
  bottomRight->setStagePoint(p);
  cornersOff();
  //sleep(1);

  cornerOn(BOTTOMLEFT);
  //sleep(1);
  p = mvision->findCircle();
  mvision->setBottomLeft(p);
  bottomLeft->setStagePoint(p);
  cornersOff();
  //sleep(1);

  cout << ">>> Finding coordinates ready" << endl;
  print();

  cout << ">>> Finding robots" << endl;
  findRobots();
  printRobots();
  for(Robot* r: robots) {
    calibrateRobot(r);
  }
  printRobots();

  //  boolean ok = stage.cornerOn(stage.TOPLEFT);
  //  mvision.findCircle();

  mvision->stageCalibrated = true;
  calibrated = true;

  cout << ">>> Stage calibration ready" << endl;
}

void Stage::calibrateRobot(Robot* r) 
{
  cout << ">> Calibrating robot '" << r->getName() << ":" << r->getId() << "'" << endl;
  r->setCheckpoint();
  
  for(int i = 0; i < 4; i++) {
    StagePoint position = findRobot(r);
    r->driveForward(250);
    //  usleep(100000);
  }
  StagePoint position = findRobot(r);
  cout << ">>> " << position.x << ", " << position.y << endl;
  int dir_from_checkpoint = r->getDirectionFromCheckpoint();
  cout << ">>>" << dir_from_checkpoint << endl;

  int time = 500;
  r->turnRight(time);
  position = findRobot(r);

  r->setCheckpoint();
  for(int i = 0; i < 4; i++) {
    StagePoint position = findRobot(r);
    r->driveForward(250);
    //  usleep(100000);
  }
  int dir = r->getDirectionFromCheckpoint();
  if(dir < dir_from_checkpoint) dir += 360;
  float diff = dir - dir_from_checkpoint;
  
  cout << ">>> Direction difference for " << time << "ms: " << diff << endl;
  cout << ">>> Approximated time for a degree: " << time/diff << endl; 

  r->setCalibratedTurnTime(time/diff);

  /*
  turnRobotTowards(r, StagePoint(0,0));
  sleep(1);
  turnRobotTowards(r, StagePoint(stageWidthPx, 0));
  sleep(1);
  turnRobotTowards(r, StagePoint(stageWidthPx, stageHeightPx));
  sleep(1);
  turnRobotTowards(r, StagePoint(0, stageHeightPx));
  */

  cout << ">>> Robot calibration ready" << endl;
}

bool Stage::turnRobotTowards(Robot* r, StagePoint p)
{
  cout << ">> Turning robot #" << r->getId() << " towards point " << p.x << ", " << p.y << endl;
  StagePoint rpos = r->getPosition();
  int rdir = r->getDirection();
  cout << "Robot position:  " << rpos.x << ", " << rpos.y << endl;
  cout << "Robot direction: " << rdir << endl;
  int angle = rpos.getAngle(&p);
  cout << "Target direction " << angle << endl;
  int diff = angle - rdir;
  cout << "Difference " << diff << endl;
  if (abs(diff) < 5) return true;
  if((diff < 180 && diff > 0) || diff < -180 ) {
    if(diff < 0) diff = -diff;
    //if(diff < 0) diff = 360 + diff;
    if(diff > 180) diff = 360 - diff;
    cout << "Turning right" << endl;
    cout << diff << endl;
    r->turnRightDegree(diff);
  } else {
    if(diff < 0) diff = -diff;
    if(diff > 180) diff = 360 - diff;
    cout << "Turning left" << endl;
    cout << diff << endl;
    r->turnLeftDegree(diff);
  }
  //      r->setCheckpoint();
  cout << ">>> Done.. Robot direction: " << r->getDirection() << endl;
}

bool Stage::cornersOn() 
{
  cout << ">> Switching all corners on" << endl;
  bool ok = false;
  if(cornersOk()) {
    cout << "->BL" << endl;
    while(!(ok = bottomLeft->switchOn()));
    //if(!ok) return false;
    cout << "->BR" << endl;
    while(!(ok = bottomRight->switchOn()));
    //if(!ok) return false;
    cout << "->TL" << endl;
    while(!(ok = topLeft->switchOn()));
    //if(!ok) return false;
    cout << "->TR" << endl;
    while(!(ok = topRight->switchOn()));
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

vector<StagePoint> Stage::findObjects()
{
  return mvision->findCirclesFromStage();
}

vector<StagePoint> Stage::findRobots()
{
  vector<StagePoint> positions;
  for(Robot* r: this->robots) {
    positions.push_back(findRobot(r));
  }
  return positions;
}

Robot* Stage::getRobot(string id)
{
  /*
  for(vector<Robot*>::iterator it = robots.begin(); it != robots.end(); ++it) {
    Robot *tmp = *it;
    if(tmp->getId().compare(id) == 0) {
      return tmp;
    }
  }
  */
  for(Robot* r: robots) {
    cout << "'" << r->getId() << "' '" << id << "'" << endl;
    string tmp = r->getId();
    if(tmp.compare(id) == 0) {
      cout << "ok" << endl;
      return r;
    }
  }
  return nullptr;
}

vector<Robot*> Stage::getRobots()
{
  return this->robots;
}

StagePoint Stage::findRobot(string id)
{
  return findRobot(getRobot(id));
}
StagePoint Stage::findRobot(Robot* r)
{
  if(r != nullptr) {
    while(!r->irOn());
    usleep(10000);
    StagePoint point = mvision->findCircleFromStage();
    if(point.x != -1 && point.y != -1)
      r->appendPosition(point);
    while(!r->irOff());
    return point;
  }

  throw no_such_robot;
}

Robot::Color Stage::getColorForRobot()
{
  while (true) {
    int r = rand() % 255;
    int g = rand() % 255;
    int b = rand() % 255;
    if(!(abs(r-g) >= 100 || abs(r-b) >= 100 || abs(g-b) >= 100)) continue;
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
    while(!(ok = bottomLeft->switchOff()));
    //if(!ok) return false;
    while(!(ok = bottomRight->switchOff()));
    //if(!ok) return false;
    while(!(ok = topLeft->switchOff()));
    //    if(!ok) return false;
    while(!(ok = topRight->switchOff()));
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
      while(!(ok = topLeft->switchOn()));
      break;
    case TOPRIGHT:
      while(!(ok = topRight->switchOn()));
      break;
    case BOTTOMLEFT:
      while(!(ok = bottomLeft->switchOn()));
      break;
    case BOTTOMRIGHT:
      while(!(ok = bottomRight->switchOn()));
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
      while(!(ok = topLeft->switchOff()));
      break;
    case TOPRIGHT:
      while(!(ok = topRight->switchOff()));
      break;
    case BOTTOMLEFT:
      while(!(ok = bottomLeft->switchOff()));
      break;
    case BOTTOMRIGHT:
      while(!(ok = bottomRight->switchOff()));
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
  cout << "=================================" << endl;
  cout << "Stage width:  " << stageWidthCm << "cm, " << stageWidthPx << "px" << endl;
  cout << "Stage height: " << stageHeightCm << "cm, " << stageHeightPx << "px" << endl;
  cout << "Top left:     " << topLeft->getX() << " " << topLeft->getY() << endl;
  cout << "Top right:    " << topRight->getX() << " " << topRight->getY() << endl;
  cout << "Bottom right: " << bottomRight->getX() << " " << bottomRight->getY() << endl;
  cout << "Bottom left:  " << bottomLeft->getX() << " " << bottomLeft->getY() << endl;
  cout << "=================================" << endl;
}

void Stage::printRobots()
{
  cout << "=================================" << endl;  
  cout << "Robots:" << endl;
  for(Robot* r: robots) {
    cout << r->getName() << endl;
    cout << "x: " << r->getX() << endl;
    cout << "y: " << r->getY() << endl;
    cout << "sdir: " << r->getShortDirection() << endl;
    cout << "ldir: " << r->getDirectionFromCheckpoint() << endl;
  }
  cout << "=================================" << endl;
}
