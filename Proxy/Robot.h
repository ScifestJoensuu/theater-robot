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

public:
	struct Color { int r; int g; int b;};
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
	StagePoint firstPointFromCheckpoint();
	vector<StagePoint> getPositions();
	bool irOn();
	bool irOff();
	bool sendMessage(string msg);
	void appendPosition(StagePoint p);
	int getDirection();
	int getShortDirection();
	int getDirectionFromCheckpoint();
	float getSpeed();
	void setCheckpoint();
	void setCheckpoint(time_t t);
	time_t getCheckpoint();
	void setColor(Color c);
	Color getColor();
	void setCalibratedTurnTime(float time);
	int diffBetweenPreviousPositions();

	bool driveForward();
	bool driveForward(int time);
	bool driveBackward();
	bool driveBackward(int time);
	bool turnRight(int time);
	bool turnRightDegree(int degree);
	bool turnLeft(int time);
	bool turnLeftDegree(int degree);
	bool stop();
 private:
	void init();
	float calibrated_turn_time; 
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
	vector<StagePoint>::iterator iteratorFromCheckpoint();

};


#endif /* ROBOT_H_ */
