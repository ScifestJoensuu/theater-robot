/*
 * PlayDirector.cpp
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#include "PlayDirector.h"

/*
void directRobotTo(Robot r);
void dircetRobotTo(int x, int y);
*/

PlayDirector::PlayDirector()
{
  script = new Script();
  tracking = false;
}

void PlayDirector::startSession(int connection)
{

}

void PlayDirector::setStage(Stage* s) 
{
  stage = s;
}

void PlayDirector::setArduinoConnection(ArduinoConnection* c)
{
  ard = c;
}

void PlayDirector::test()
{
  /*
  script = new Script();
  script->addCommand(new ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 50, 50));
  script->addCommand(new ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 100, 100));
  script->addCommand(new ScriptCommand("1", ScriptCommand::DRIVE, ScriptCommand::POINT, 150, 50));
  executeScript();
  */
  script = new Script("{\"id\":\"1\",\"x\":\"50\",\"y\":\"10\"}");
}

void PlayDirector::executeScript()
{
  cout << ">> Executing script.." << endl;
  ScriptCommand *cmd;
  while((cmd = script->getNextCommand()) != nullptr) {
    executeCommand(cmd);
  }
  cout << ">> Script ended" << endl;
}

void PlayDirector::executeScript(string s)
{
  //  cout << "!!" << endl;
  Script* script = new Script(s);
  cout << ">> Executing script.." << endl;
  ScriptCommand *cmd;
  while((cmd = script->getNextCommand()) != nullptr) {
    executeCommand(cmd);
  }
  cout << ">> Script ended" << endl;
}

void PlayDirector::executeCommand(ScriptCommand *cmd)
{
  bool ok = false;
  while(!ok) {
    switch (cmd->cmd_t) {
    case ScriptCommand::DRIVE:
      switch (cmd->target_t) {
      case ScriptCommand::POINT:
	stage->getRobot(cmd->robot_id)->setCheckpoint();
	ok = directRobotTo(cmd->robot_id, StagePoint(cmd->target_x, cmd->target_y));
	break;
      case ScriptCommand::ROBOT:
	stage->getRobot(cmd->robot_id)->setCheckpoint();
	ok = directRobotTo(cmd->robot_id, cmd->target_id);
	break;
      }
      break;
    }
  }
}

void PlayDirector::setMVision(MVision* m) 
{
  this->mvision = m;
}

bool PlayDirector::directRobotTo(string robot_id, StagePoint p)
{
  cout << ">> Directing robot to " << p.x << ", " << p.y << endl;
  /*
  if(stage->robotAtPoint(robot_id, &p)) return true;
  Robot* robot = stage->getRobot(robot_id);
  StagePoint robot_location = stage->findRobot(robot);
  int distance = robot_location.getDistance(&p);
  int robot_dir = robot->getShortDirection();
  int target_dir = robot_location.getAngle(&p);
  int dir_change = target_dir - robot_dir;
  
  cout << ">>> Robot position:   " << robot_location.x << ", " << robot_location.y << endl;
  cout << ">>> Robot direction:  " << robot_dir << endl;
  cout << ">>> Target direction: " << target_dir << endl;
  cout << ">>> Direction change: " << dir_change << endl;
  
  if(dir_change < 0) {
    robot->turnLeft(dir_change);
  } else if(dir_change > 0) {
    robot->turnLeft(dir_change);
  }
  robot->driveForward();
  
    thread send(&PlayDirector::sendRobotLocation, this, robot);
  //send.join();
  // wait
  
  return directRobotTo(robot_id, p);
  */
  Robot* r = stage->getRobot(robot_id);
  while(!r->irOn());
  tracking = true;
  //thread t(&PlayDirector::trackRobot, this, r);
  while(true) {
    //StagePoint tmp = stage->findRobot(r);

    StagePoint point = mvision->findCircleFromStage();
    if(point.x != -1 && point.y != -1)
      r->appendPosition(point);
    StagePoint tmp = r->getPosition();

    int distance = p.getDistance(&tmp);

    cout << "Robot pos: " << tmp.x << ", " << tmp.y << endl;
    cout << "Distance " << distance << endl;

    if(distance < 20) {
      cout << "Robot at the destination" << endl;
      break;
    }
    int pdiff = r->diffBetweenPreviousPositions();
    cout << "pdiff: " << pdiff << endl;
    //  if(pdiff > 5)
      stage->turnRobotTowards(r, p);
    r->driveForward(250);
  }
  //  r->stop();
  tracking = false;
  //t.join();
  while(!r->irOff());

}

void PlayDirector::trackRobot(Robot* r) 
{
  while(tracking) {
    StagePoint point = mvision->findCircleFromStage();
    StagePoint rpos = r->getPosition();
    cout << point.x << ", " << point.y << endl;
    if(point.x != -1 && point.y != -1 && point.getDistance(&rpos) > 5)
      r->appendPosition(point);
    usleep(100000);
  }
}

bool PlayDirector::directRobotTo(string robot_id, string target_id)
{

}

bool PlayDirector::sendRobotLocation(Robot* r) {
  for(int i = 0; i < 5; i++) {
    bool ok = ard->sendRobotLocation(r);
    if(ok) return true;
  }
  cout << "!! Sending robot position failed.." << endl;
  return false;
}

bool PlayDirector::sendAllRobotLocations() {
  vector<Robot*> robots = stage->getRobots();
  bool ok = true;
  for(vector<Robot*>::iterator it = robots.begin(); it != robots.end(); it++)
    {
      Robot* r = *it;
      bool tmp = sendRobotLocation(r);
      if(ok) ok = tmp; // if ok == true, set ok to tmp (if false appears, it stays)
    }
  return ok;
}
