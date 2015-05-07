#ifndef BTM_H
#define BTM_H
#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <vector>
#include "BluetoothConnection.h"
#include "Stage.h"

using namespace std;

class BluetoothManager
{
  //  vector<BluetoothConnection*> corners;
  Stage* stage;
  vector<BluetoothConnection*> robots;
 public:
  //  vector<Bluetooth
  vector<BluetoothConnection*> scanDevices();
  void setStage(Stage* stage);
  Stage* getStage();
};

#endif
