#include <iostream>
#include "MVision.h"
#include "BluetoothManager.h"
#include "Stage.h"
#include <thread>

using namespace std;

MVision mvision;
BluetoothManager btman;
Stage stage;

void showImage();
void calibrateStage();

int cam = 0;

bool exiting = false;

int main(int argc, char *argv[]) 
{
  cout << ">> Starting RobotStory" << endl;
  cout << ">> Args: " << argc << endl;
  if(argc == 2) cam = *argv[1] - '0';

  //  cout << c.getStatus() << endl;
  btman.setStage(&stage);
  calibrateStage();

  return 0;
}

void calibrateStage() 
{
  vector<BluetoothConnection*> devices = btman.scanDevices();
  cout << "TL: " << stage.getTopLeftCorner()->getBluetoothConnection()->getAddress() << endl;
  cout << "TR: " << stage.getTopRightCorner()->getBluetoothConnection()->getAddress() << endl;
  cout << "BL: " << stage.getBottomLeftCorner()->getBluetoothConnection()->getAddress() << endl;
  cout << "BR: " << stage.getBottomRightCorner()->getBluetoothConnection()->getAddress() << endl;

  bool ok = stage.cornersOn();
  if(!ok) {
    cout << ">> Could not switch on corners, shutting down" << endl;
    exit(0);
  }
  mvision.init(cam);

  thread mav(showImage);
  //showImage();
 
  ok = stage.cornersOff();
  for(int i = 0; i < 5; i++) {
    ok = stage.cornerOn(stage.TOPLEFT);
    sleep(1);
    ok = stage.cornerOff(stage.TOPLEFT);
    ok = stage.cornerOn(stage.TOPRIGHT);
    sleep(1);
    ok = stage.cornerOff(stage.TOPRIGHT);
    ok = stage.cornerOn(stage.BOTTOMRIGHT);
    sleep(1);
    ok = stage.cornerOff(stage.BOTTOMRIGHT);
    ok = stage.cornerOn(stage.BOTTOMLEFT);
    sleep(1);
    ok = stage.cornerOff(stage.BOTTOMLEFT);
    //cout << "next.." << endl;
    for(int j = 0; j < 3; j++) {
      ok = stage.cornersOn();
      sleep(1);
      ok = stage.cornersOff();
      sleep(1);
    }
  }
  cout << "Tests ready" << endl;
  //  boolean ok = stage.cornerOn(stage.TOPLEFT);
  //  mvision.findCircle();
  exiting = true;
  mav.join();
}

void showImage() 
{
  while(!exiting) {
    //cout << "thread.." << endl;
    mvision.showImage();
    int c = cvWaitKey(5);
    if(c == 27) exit(0);
  }
}
