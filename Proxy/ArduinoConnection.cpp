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
	const char* sys = "stty -F " + port + " cs8 9600 ignbrk -brkint -icrnl -imaxbel -opost -onlcr -isig -icanon -iexten -echo -echoe -echok -echoctl -echokenoflsh -ixon -crtscts";
	system(sys);	//Activates the tty connection with the Arduino

	//ifstream Arduino_Input("/dev/ttyACM0");
	instream = ifstream(port);
	outstream = ofstream(port);
	//ofstream Arduino_Output("/dev/ttyACM0");
}

void ArduinoConnection::waitForConnection()
{
	char* data;
	long int time = time(NULL);

	while(time(NULL)-time < 5){}	//Wait five seconds for the Arduino to start up

	bool ok = false;
	while(!ok)
	{
			time = time(NULL);
			while(time(NULL)-time < 1){}	//wait one second to get good numbers into the Arduino stream
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

bool ArduinoConnection::sendRobotLocation(Robot* r)
{
	string msg = "{[" + id + "]\"id\":\"" + r->getId() + "\",\"x\":\"" + r->getPosition().x + "\", \"y\":\"" + r->getPosition().y + "\"}";
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
