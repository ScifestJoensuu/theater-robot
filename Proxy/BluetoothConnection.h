#ifndef BTC_H
#define BTC_H
#pragma once

#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include <unistd.h>
#include <iostream>
#include <sstream>

using namespace std;

class BluetoothConnection
{
  int msg_counter;
  int try_counter;
  int s;
  int status;
  string address;
  string buildMessage(string msg);
 public:
  BluetoothConnection();
  BluetoothConnection(string addr);
  int init(string addr);
  void initConnection();
  bool connectBluetooth();
  void closeConnection();
  int getStatus();
  string getAddress();
  int sendMessage(string msg);
  bool sendMessageAndWaitForResponse(string msg);
  string readMessage();
};

#endif
