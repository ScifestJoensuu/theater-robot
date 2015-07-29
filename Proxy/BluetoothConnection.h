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
  string name;
  string address;
  string buildMessage(string msg);
 public:
  BluetoothConnection();
  BluetoothConnection(string name, string addr);
  int init(string addr);
  void initConnection();
  bool connectBluetooth();
  void closeConnection();
  int getStatus();
  string getAddress();
  string getName();
  int sendMessage(string msg);
  bool sendMessageAndWaitForResponse(string msg);
  bool sendMessageAndWaitForResponse(string msg, int ms);
  string readMessage();
};

#endif
