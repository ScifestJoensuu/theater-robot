/*
 * Robot.h
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#ifndef ROBOT_H_
#define ROBOT_H_

#include "BluetoothConnection.h"
#include "StagePoint.h"

using namespace std;

class Robot
{
	string id;
	string name;
	int x;
	int y;
	BluetoothConnection* connection;
public:
	Robot();
	void setId(string id);
	string getId();
	void setName(string n);
	string getName();
	void setBT(BluetoothConnection* bt);
	BluetoothConnection* getBT();
	void setX(int x);
	void setY(int y);
	void setStagePoint(StagePoint p);
	int getX();
	int getY();
	StagePoint getStagePoint();
};


#endif /* ROBOT_H_ */
