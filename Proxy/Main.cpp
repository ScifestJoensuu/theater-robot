
#include <iostream>
#include "MVision.h"
#include "BluetoothManager.h"
#include "Stage.h"
#include "Server.h"
#include "PlayDirector.h"
#include <thread>
#include "ArduinoConnection.h"
#include <string>


using namespace std;

MVision mvision;
BluetoothManager btman;
//Server server;
Stage stage;
PlayDirector director;
ArduinoConnection con;

void showImage();
void readCmd();
void trackRobots();
void executeCmd(string cmd);

string previous_cmd;

int cam = 0;
int port = 11235;

bool exiting = false;

vector<Robot*> robots;


int main(int argc, char *argv[]) 
{
  cout << ">> Starting RobotStory" << endl;
  cout << ">> Args: " << argc << endl;
  if(argc >= 2) cam = *argv[1] - '0';
  if(argc >= 3) port = *argv[2] - '0';

  mvision.init(cam);

  thread mav(showImage);

  stage.setBluetoothManager(&btman);
  stage.setMVision(&mvision);

  stage.calibrateStage();
  robots = stage.getRobots();
  
  cout << "Robots: " << robots.size() << endl;
  for(Robot* r: robots)
    cout << r->getName() << " " << r->getId() << endl;
  
  //con.init();
  //con.waitForConnection();
  //director.setArduinoConnection(&con);
  //director.sendAllRobotLocations();
  //director.test();
  director.setStage(&stage);
  director.setMVision(&mvision);

  thread track(trackRobots);

  
  while(true) {
    readCmd();
	//int c = cvWaitKey(50);
	//if(c == 27) exit(0);
	/*
	stage.findRobots();
	for(Robot* r: robots) {
	  cout << r->getName() << " - x: " << r->getX() << ", y: " << r->getY() << endl;
	}
	*/
	//cout << "moi" << endl;
    //showImage();
    //server.init(port);
    
    //string script = con.receiveScript();
    //cout << ">> Received a message: '" << script << "'" << endl;
    //if(script.find("msg_rec_timeout") == string::npos)director.executeScript(script);
    
    //director.startSession(0);
    // server init
    // wait connections
  
    // receive destination
    // guide robot
    //int c = cvWaitKey(50);
    //if(c == 27) exit(0);
  }
  
  exiting = true;
  mav.join();
  track.join();
  return 0;
}

void showImage() 
{
  while(!exiting) {
    mvision.showImage();
    if(stage.calibrated)
          stage.findObjects();
    int c = cvWaitKey(10);
    if(c == 27) exit(0);
  }
}

void trackRobots() 
{
  while(!exiting) {
    for(Robot* r: robots) {
      /*      r->irOn();
	      sleep(1);
	      r->irOff();
	      sleep(1);*/
      //stage.findRobot(r);
    }
    usleep(100000);
  }
}

void readCmd()
{
  string cmd;
  cout << ">> ";
  getline(cin, cmd);
  executeCmd(cmd);
}

void executeCmd(string cmd) {
  if(cmd.compare("help") == 0) {
    cout << ">>> help here!" << endl;
  } else if(cmd.compare("calibrate_stage") == 0) {
    stage.calibrateStage();
  } else if(cmd.compare("robots") == 0) {
    if(robots.size() <= 0) cout << ">>> no robots" << endl;
    stage.printRobots();
    /*
    for(Robot* r: robots) { 
      cout << ">>> " << r->getName() << ", id: " << r->getId() << ", pos: " << r->getX() << ", " << r->getY() << endl;
    }
    */
  } else if(cmd.find("calibrate_robot") != string::npos) {
    string syntax = ">>> syntax: calibrate_robot id";
    int first_whitespace = cmd.find(" ");
    if(first_whitespace == string::npos) {
      cout << syntax << endl;
      return ;
    } 
    string id = cmd.substr(first_whitespace+1);
    cout << ">>> id: " << id << endl;
    if(id.empty()) {
      cout << syntax << endl;
      return ;
    }
    Robot* r = stage.getRobot(id);
    if(r != nullptr) {
      cout << "r" << r->getName() << endl;
      stage.calibrateRobot(r);
      stage.printRobots();
    } else {
       cout << "null" << endl;
    }
  } else if(cmd.find("send") != string::npos) {
    string syntax = ">>> syntax: send robot_id msg";
    int first_whitespace = cmd.find(" ");
    if(first_whitespace == string::npos) {
      cout << syntax << endl;
      return ;
    } 
    int second_whitespace = cmd.find(" ", first_whitespace+1);
    if(second_whitespace == string::npos) {
      cout << syntax << endl;
      return ;
    } 
    string id = cmd.substr(first_whitespace+1, second_whitespace-first_whitespace-1);
    string msg = cmd.substr(second_whitespace+1);
    cout << ">>> id: " << id << endl;
    cout << ">>> msg: " << msg << endl;
    if(id.empty() || msg.empty()) {
      cout << syntax << endl;
      return ;
    }
    Robot* r = stage.getRobot(id);
    if(r != nullptr) {
      cout << "r" << r->getName() << endl;
      r->sendMessage(msg);
    } else {
       cout << "null" << endl;
    }
  } else if(cmd.find("test") != string::npos) {
    director.directRobotTo("1", StagePoint(50,50));
    director.directRobotTo("1", StagePoint(stage.stageWidthPx-50, 50));
    director.directRobotTo("1", StagePoint(stage.stageWidthPx-50, stage.stageHeightPx-50));
    director.directRobotTo("1", StagePoint(50,stage.stageHeightPx-50));
  } else if(cmd.size() == 1 && cmd.find("p") != string::npos) {
    cout << "Executing previous command '" << previous_cmd << "'" << endl;
    executeCmd(previous_cmd);
  } else {
    cout << ">>> command not recognized.." << endl;
  }
  
  if(cmd.size() > 1) {
    previous_cmd = cmd;
  }
}
