/*
 * ArduinoConnection.h
 *
 *  Created on: 25.5.2015
 *      Author: mikko
 */

#ifndef ARDUINOCONNECTION_H_
#define ARDUINOCONNECTION_H_

#include "Robot.h"
#include<iostream>
#include<fstream>
#include<cstdlib>
#include <unistd.h>

using namespace std;

class ArduinoConnection
{
    private:
		ifstream instream;
		ofstream outstream;
		int id;
		bool waitForResponse(int id);
    public:
        ArduinoConnection();
        ~ArduinoConnection();
        void init();
        void init(string port);
        void waitForConnection();
        string receiveScript();
        bool sendMessage(string msg);
        string readMessage();
        bool sendRobotLocation(Robot* r);
};



#endif /* ARDUINOCONNECTION_H_ */
