/*
 * Robot.cpp
 *
 *  Created on: 12.5.2015
 *      Author: mikko
 */

#include "Robot.h"

Robot::Robot()
{
	id = "-1";
	name = "unnamed_robot";
	//x = 0;
	//y = 0;
}

Robot::Robot(BluetoothConnection *con)
{
	id = "-1";
	name = "unnamed_robot";
	//x = 0;
	//y = 0;
	connection = con;
}

void Robot::setId(string i)
{
	id = i;
}

void Robot::setName(string n)
{
	name = n;
}

void Robot::setBT(BluetoothConnection* bt)
{
	connection = bt;
}

string Robot::getId()
{
	return id;
}

string Robot::getName()
{
	return name;
}

BluetoothConnection* Robot::getBT()
{
	return connection;
}
/*
void Robot::setX(int x)
{
	this->x = x;
}

void Robot::setY(int y)
{
	this->y = y;
}

void Robot::setStagePoint(StagePoint p)
{
	this->x = p.x;
	this->y = p.y;
}
*/
int Robot::getX()
{
	return positions.back().x;
}

int Robot::getY()
{
	return positions.back().y;
}

StagePoint Robot::getPosition()
{
	return positions.back();
}

bool Robot::irOn()
{
	return connection->sendMessage("led_on");
}
bool Robot::irOff()
{
	return connection->sendMessage("led_off");
}
bool Robot::driveForward()
{
	return connection->sendMessage("df");
}
bool Robot::driveForward(int time)
{
	return connection->sendMessage("df:" + time);
}
bool Robot::driveBackward()
{
	return connection->sendMessage("db");
}
bool Robot::driveBackward(int time)
{
	return connection->sendMessage("db:" + time);
}
bool Robot::turnRight(int degree)
{
	return connection->sendMessage("tr:" + degree);
}
bool Robot::turnLeft(int degree)
{
	return connection->sendMessage("rl:" + degree);
}
bool Robot::stop()
{
	return connection->sendMessage("s");
}

void Robot::appendPosition(StagePoint p)
{
	StagePoint* previous = positions.back();
	positions.push_back(p);
	vector<StagePoint*>::iterator it = iteratorFromCheckpoint();
	/*for(; it != positions.end(); it++) {

	}*/
	StagePoint* tmp = *it;
	this->shortDir = previous->getAngle(&p);
	this->dirFromCheckpoint = tmp->getAngle(&p);
}

void Robot::setCheckpoint()
{
	checkpoint = time(nullptr);
}

void Robot::setCheckpoint(time_t t)
{
	checkpoint = t;
}

time_t Robot::getCheckpoint()
{
	return checkpoint;
}

int Robot::getShortDirection()
{
	return this->shortDir;
}

int Robot::getDirectionFromCheckpoint()
{
	return this->dirFromCheckpoint;
}

vector<StagePoint*>::iterator Robot::iteratorFromCheckpoint()
{
	if(checkpoint == NULL) return positions.begin();
	vector<StagePoint*>::iterator it;
	for(it = positions.end(); it != positions.begin(); --it) {
		StagePoint* tmp = *it;
		if(tmp->getTime() < checkpoint) {
			it++;
			break;
		}
	}
	return it;
}


