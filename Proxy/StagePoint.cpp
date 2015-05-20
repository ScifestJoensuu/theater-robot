/*

 * StagePoint.cpp
 *
 *  Created on: 13.5.2015
 *      Author: mikko
 */

#include "StagePoint.h"

StagePoint::StagePoint()
{}
StagePoint::StagePoint(int x, int y)
{
	timestamp = time(nullptr);
	this->x = x;
	this->y = y;
}

int StagePoint::getAngle(StagePoint* p)
{
	float m = (p->y - this->y) / (p->x - this->x);
	int angle = atan(m);
	return angle;
}

int StagePoint::getDistance(StagePoint* p)
{
	double dist = sqrt(pow(p->x - this->x, 2) + pow(p->y - this->y, 2));
	return dist;
}

time_t StagePoint::getTime()
{
	return timestamp;
}
