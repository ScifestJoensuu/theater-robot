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
	x = 0;
	y = 0;
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

int Robot::getX()
{
	return x;
}

int Robot::getY()
{
	return y;
}

StagePoint Robot::getStagePoint()
{
	return StagePoint(x, y);
}
