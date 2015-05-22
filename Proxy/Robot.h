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
#include <vector>
#include <ctime>
using namespace std;

class Robot
{
	struct Color { int r; int g; int b;};
	string id;
	string name;
	int dirFromCheckpoint;
	int shortDir;
	float speed;
	time_t checkpoint;
	Color color;
	//int x;
	//int y;
	vector<StagePoint> positions;
	BluetoothConnection* connection;
	vector<StagePoint*>::iterator Robot::iteratorFromCheckpoint();
public:
	Robot();
	Robot(BluetoothConnection* con);
	void setId(string id);
	string getId();
	void setName(string n);
	string getName();
	void setBT(BluetoothConnection* bt);
	BluetoothConnection* getBT();
	//void setX(int x);
	//void setY(int y);
	//void setStagePoint(StagePoint p);
	int getX();
	int getY();
	StagePoint getPosition();
	bool irOn();
	bool irOff();
	void appendPosition(StagePoint p);
	int getShortDirection();
	int getDirectionFromCheckpoint();
	float getSpeed();
	void setCheckpoint();
	void setCheckpoint(time_t t);
	time_t getCheckpoint();
	void setColor(Color c);
	Color getColor();

	bool driveForward();
	bool driveForward(int time);
	bool driveBackward();
	bool driveBackward(int time);
	bool turnRight(int degree);
	bool turnLeft(int degree);
	bool stop();
};


#endif /* ROBOT_H_ */
