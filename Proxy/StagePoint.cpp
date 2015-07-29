/*

 * StagePoint.cpp
 *
 *  Created on: 13.5.2015
 *      Author: mikko
 */

#include "StagePoint.h"

StagePoint::StagePoint()
{
  timestamp = time(nullptr);
  this->x = -1;
  this->y = -1;
}
StagePoint::StagePoint(int x, int y)
{
	timestamp = time(nullptr);
	this->x = x;
	this->y = y;
}

int StagePoint::getAngle(StagePoint* p)
{
  //  cout << "from: " << this->x << ", " << this->y << endl;
  //  cout << "to:   " << p->x << ", " << p->y << endl;
  if(this->x < 0 || this->y < 0) return -1;
  float m = ((float)(p->y - this->y)) / ((float)(p->x - this->x));
  float tx = (float)(p->x - this->x);
  float ty = (float)(p->y - this->y);
  //float angle = atan2(m);
  float angle = atan2(ty, tx);
  int r = angle*(180/3.14);
  if(r < 0) r = 360 + r;
  //  cout << "angle: " << r << endl;
  return abs(r);
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

bool StagePoint::compareTo(StagePoint* p) 
{
  if(x == p->x  && y == p->y) return true;
  return false;
}
