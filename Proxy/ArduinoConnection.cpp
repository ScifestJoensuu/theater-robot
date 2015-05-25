/*
 * ArduinoConnection.cpp
 *
 *  Created on: 25.5.2015
 *      Author: mikko
 */

#include "ArduinoConnection.h"

ArduinoConnection::ArduinoConnection()
{

}

ArduinoConnection::~ArduinoConnection()
{
  instream.close();
  outstream.close();
}

void ArduinoConnection::init()
{
	init("/dev/ttyACM0");
}

void ArduinoConnection::init(string port)
{
	id = 0;
	string sys = "stty -F " + port + " cs8 9600 ignbrk -brkint -icrnl -imaxbel -opost -onlcr -isig -icanon -iexten -echo -echoe -echok -echoctl -echokenoflsh -ixon -crtscts";
	system(sys.c_str());	//Activates the tty connection with the Arduino

	//ifstream Arduino_Input("/dev/ttyACM0");
	instream.open(port.c_str());
	outstream.open(port.c_str());
	//ofstream Arduino_Output("/dev/ttyACM0");
}

void ArduinoConnection::waitForConnection()
{
	char* data;
	long int t = time(nullptr);

	while(time(nullptr)-t < 5){}	//Wait five seconds for the Arduino to start up

	bool ok = false;
	while(!ok)
	{
			t = time(nullptr);
			while(time(nullptr)-t < 1){}	//wait one second to get good numbers into the Arduino stream
			while(!instream.eof())	//while the eof flage isn't set
			{
				instream >> data;
				cout << data << endl;
			}
			instream.clear();	//eof flag won't clear itself
		}
}

string ArduinoConnection::receiveScript()
{

}

string ArduinoConnection::readMessage()
{
	string data;
	while(!instream.eof())	//while the eof flage isn't set
	{
		instream >> data;
		cout << data << endl;
		if(data.find("}", 0) != string::npos) break;
	}
	if(instream.eof()) instream.clear();
	data = data.substr(data.find("{"), string::npos);
	data = data.substr(0, data.find("}"));
	return data;
}

bool ArduinoConnection::sendMessage(string msg)
{
  outstream << msg;
}

bool ArduinoConnection::sendRobotLocation(Robot* r)
{
  string msg = "{[" + to_string(id) + "]\"id\":\"" + r->getId() + "\",\"x\":\"" + to_string(r->getPosition().x) + "\", \"y\":\"" + to_string(r->getPosition().y) + "\"}";
	sendMessage(msg);
	return waitForResponse(id);
}

bool ArduinoConnection::waitForResponse(int id)
{
	for(int i = 0; i < 5; i++)
	{
		string msg = readMessage();
		if(msg.find(id) != string::npos) {
			id++;
			return true;
		}
		usleep(200000);
	}
	return false;
}
