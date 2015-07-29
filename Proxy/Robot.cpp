/*
 * Robot.cpp
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#include "Robot.h"

Robot::Robot()
{
  init();
}

Robot::Robot(BluetoothConnection *con)
{
  init();
  connection = con;
}

void Robot::init()
{
  id = "-1";
  name = "unnamed_robot";
  color.r = 0;
  color.g = 0;
  color.b = 255;
  calibrated_turn_time = 15;
}

void Robot::setId(string i)
{
  this->id = i;
}

void Robot::setName(string n)
{
  this->name = n;
}

void Robot::setBT(BluetoothConnection* bt)
{
  this->connection = bt;
}

void Robot::setCalibratedTurnTime(float time) 
{
  this->calibrated_turn_time = time;
}

string Robot::getId()
{
  return this->id;
}

string Robot::getName()
{
  return this->name;
}

BluetoothConnection* Robot::getBT()
{
  return this->connection;
}
/*
  void Robot::setX(int x)
  {
  this->x = x;
  }

  void Robot::setY(int y)
  {
  this->y = y;
  }

  void Robot::setStagePoint(StagePoint p)
  {
  this->x = p.x;
  this->y = p.y;
  }
*/
int Robot::getX()
{
  if(positions.size() == 0) return -1;
  return positions.back().x;
}

int Robot::getY()
{
  if(positions.size() == 0) return -1;
  return positions.back().y;
}

StagePoint Robot::getPosition()
{
  if(positions.size() == 0) return StagePoint(-1,-1);
  return positions.back();
}

vector<StagePoint> Robot::getPositions()
{
  return positions;
}

bool Robot::irOn()
{
  //  cout << "Turning robot '" << id << "' led on" << endl;
  return connection->sendMessageAndWaitForResponse("c:led:b:1");
  //  return connection->sendMessage("moi");
}
bool Robot::irOff()
{
  //  cout << "Turning robot '" << id << "' led off" << endl;
  return connection->sendMessageAndWaitForResponse("c:led:b:0");
  //	return connection->sendMessage("hei");
}
bool Robot::driveForward()
{
  return connection->sendMessageAndWaitForResponse("c:df:b:1");
}
bool Robot::driveForward(int ms)
{
  return connection->sendMessageAndWaitForResponse("c:df:t:"+to_string(ms), ms);
}
bool Robot::driveBackward()
{
  return connection->sendMessage("c:db:b:1");
}
bool Robot::driveBackward(int time)
{
  return connection->sendMessage("c:db:t:" + time);
}
bool Robot::turnRight(int time)
{
  return connection->sendMessageAndWaitForResponse("c:dr:t:" + to_string(time), time);
}
bool Robot::turnRightDegree(int degree)
{
  int time = degree * calibrated_turn_time;
  bool ok = connection->sendMessageAndWaitForResponse("c:dr:t:" + to_string(time), time);
  if(ok) {
    setCheckpoint();
    int tmp = this->dirFromCheckpoint;
    tmp = tmp + degree;
    if(tmp > 360) tmp = tmp - 360;
    this->dirFromCheckpoint = tmp;
    this->shortDir = tmp;
  }
  return ok;
  // return connection->sendMessageAndWaitForResponse("c:dr:d:" + degree);
}
bool Robot::turnLeft(int time)
{
  return connection->sendMessageAndWaitForResponse("c:dl:t:" + to_string(time));
}
bool Robot::turnLeftDegree(int degree)
{
  int time = degree * calibrated_turn_time;
  bool ok = connection->sendMessageAndWaitForResponse("c:dl:t:" + to_string(time), time);
  if(ok) {
    setCheckpoint();
    int tmp = this->dirFromCheckpoint;
    tmp = tmp - degree;
    if(tmp < 0) tmp = 360 + tmp;
    this->dirFromCheckpoint = tmp;
    this->shortDir = tmp;
  }
  return ok;
}
bool Robot::stop()
{
  return connection->sendMessageAndWaitForResponse("c:ds:b:1");
}

bool Robot::sendMessage(string msg) 
{
  return connection->sendMessage(msg);
}

void Robot::appendPosition(StagePoint p)
{
    StagePoint previous;
    if(positions.size() > 0) {
    previous = positions.back();
  }
  positions.push_back(p);
  //vector<StagePoint>::iterator it = iteratorFromCheckpoint();
  //  for(; it != positions.end(); it++) {
  //  cout << "." << endl;
  //}
  //  StagePoint tmp = *it;
  //cout << tmp.x << ", " << tmp.y << endl;
  StagePoint tmp = firstPointFromCheckpoint();
  if(previous.x >= 0 && previous.y >= 0) {
    if(!previous.compareTo(&p)) this->shortDir = previous.getAngle(&p);
    if(!tmp.compareTo(&p)) this->dirFromCheckpoint = tmp.getAngle(&p);
    cout << "Short angle: " << shortDir << endl;
    cout << "Long andle: " << dirFromCheckpoint << endl;
  }
}

void Robot::setCheckpoint()
{
  checkpoint = time(nullptr);
}

void Robot::setCheckpoint(time_t t)
{
  checkpoint = t;
}

time_t Robot::getCheckpoint()
{
  return checkpoint;
}

int Robot::getShortDirection()
{
  return this->shortDir;
}

int Robot::getDirectionFromCheckpoint()
{
  return this->dirFromCheckpoint;
}
int Robot::getDirection()
{
  return this->dirFromCheckpoint;
}
Robot::Color Robot::getColor()
{
  return color;
}

void Robot::setColor(Robot::Color c)
{
  color = c;
}

int Robot::diffBetweenPreviousPositions() {
  if(positions.size() < 2) return 0;
  StagePoint p1 = positions[positions.size()-1];
  StagePoint p2 = positions[positions.size()-2];
  return p1.getDistance(&p2);
}

StagePoint Robot::firstPointFromCheckpoint() 
{
  StagePoint tmp = positions.front();
  for(StagePoint p: positions) {
    if(p.getTime() > checkpoint) return tmp;
    tmp = p;
  }
  return tmp;
}

vector<StagePoint>::iterator Robot::iteratorFromCheckpoint()
{
  if(positions.size() <= 1) return positions.begin();
  if(checkpoint <= 0) return positions.begin();
  vector<StagePoint>::iterator it = positions.end();
  for(; it != positions.begin(); --it) {
    StagePoint tmp = *it;
    if(tmp.getTime() < checkpoint) {
      it++;
      break;
    }
  }
  return it;
}


