#include <iostream>
#include "MVision.h"
#include "BluetoothManager.h"
#include "Stage.h"
#include "Server.h"
#include "PlayDirector.h"
#include <thread>
#include "ArduinoConnection.h"

using namespace std;

MVision mvision;
BluetoothManager btman;
//Server server;
Stage stage;
PlayDirector director;
ArduinoConnection con;

void showImage();

int cam = 0;
int port = 11235;

bool exiting = false;

int main(int argc, char *argv[]) 
{
  cout << ">> Starting RobotStory" << endl;
  cout << ">> Args: " << argc << endl;
  if(argc >= 2) cam = *argv[1] - '0';
  if(argc >= 3) port = *argv[2] - '0';

  mvision.init(cam);

  thread mav(showImage);

  //  cout << c.getStatus() << endl;
  //btman.setStage(&stage);
  stage.setBluetoothManager(&btman);
  stage.setMVision(&mvision);

  stage.calibrateStage();

  con.init();
  con.waitForConnection();
  director.setArduinoConnection(&con);
  director.sendAllRobotLocations();
  while(true) {
	  //server.init(port);
	  string script = con.receiveScript();
	  cout << ">> Received a message: '" << script << "'" << endl;
	  director.executeScript(script);
	  //director.startSession(0);
  // server init
  // wait connections
  
  // receive destination
  // guide robot
	  int c = cvWaitKey(5);
	  if(c == 27) exit(0);
  }
  
  exiting = true;
  mav.join();

  return 0;
}

void showImage() 
{
  while(!exiting) {
    //cout << "thread.." << endl;
    mvision.showImage();
    if(stage.calibrated)
    	mvision.showSubImage();
    int c = cvWaitKey(5);
    if(c == 27) exit(0);
  }
}
